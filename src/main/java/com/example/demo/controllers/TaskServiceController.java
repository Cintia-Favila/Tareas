package com.example.demo.controllers;
import com.example.demo.rabbitMQ.Producer;
import com.example.demo.request.TaskRequest;
import com.example.demo.response.TaskResponse;
import com.example.demo.services.TaskService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskServiceController {

    @Autowired
    private TaskService taskService;
    private Producer producer;
    public TaskServiceController(Producer producer) {
        this.producer = producer;
    }


    @PostMapping("/create")
    public ResponseEntity<TaskResponse> createTaskController(@RequestBody TaskRequest taskRequest) {
        try {
            TaskResponse taskResponse = taskService.createTask(taskRequest);
            producer.createTask(taskResponse.toString());
            taskService.sendNotification("Nueva tarea creada", "Se ha creado una nueva tarea");
            return ResponseEntity.status(HttpStatus.CREATED).body(taskResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<TaskResponse>> getUserTasksController() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            List<TaskResponse> userTasks = taskService.getUserTasks(username);
            return ResponseEntity.ok(userTasks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PatchMapping("/update/{idTask}")
    public ResponseEntity<String> updateTaskController(@PathVariable Integer idTask) {
        try {
            boolean completed = taskService.updateTaskById(idTask);
            if (completed) {
                producer.updateTask("Task completed, id: " + idTask.toString());
                taskService.sendNotification("Tarea completada", "Se ha completado una tarea");
                return ResponseEntity.ok("Task completed ");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to complete the task");
        }
    }

    @DeleteMapping("/delete/{idTask}")
    public ResponseEntity<String> deleteTaskController(@PathVariable Integer idTask) {
        try {
            boolean deleted = taskService.deleteTaskById(idTask);
            if (deleted) {
                producer.deleteTask("Task deleted, id: " + idTask.toString());
                taskService.sendNotification("Tarea eliminada", "Se ha eliminado una tarea");
                return ResponseEntity.ok("Task deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete the task");
        }
    }

    @GetMapping("/generateReport")
    public ResponseEntity<byte[]> generateReportController(HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        byte[] excelData = taskService.generateExcelReport(username);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=report.xlsx");
        response.setContentLength(excelData.length);

        producer.generateReport("Report generated for " + username);
        taskService.sendNotification("Reporte generado", "Se ha generado un nuevo reporte");

        return ResponseEntity.ok().body(excelData);
    }
}