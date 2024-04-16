package com.example.demo.servicesImpl;
import com.example.demo.models.TaskModel;
import com.example.demo.models.UserModel;
import com.example.demo.repositories.TaskJpaRepository;
import com.example.demo.repositories.UserJpaRepository;
import com.example.demo.request.TaskRequest;
import com.example.demo.response.ReportDataResponse;
import com.example.demo.response.TaskResponse;
import com.example.demo.services.TaskService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskJpaRepository taskRepository;

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private FirebaseMessaging firebaseMessaging;

    @Override
    @CacheEvict(cacheNames = {"getUserTasks", "generateExcelReport"}, allEntries = true)
    public TaskResponse createTask(TaskRequest taskRequest) {
        TaskModel taskModel = new TaskModel();

        taskModel.setIdTask(null);
        taskModel.setName(taskRequest.getName());
        taskModel.setDescription(taskRequest.getDescription());
        taskModel.setCreationDate(LocalDate.now());
        taskModel.setCompleted(false);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Optional<UserModel> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            UserModel user = userOptional.get();
            taskModel.setUser(user);

            taskModel = taskRepository.save(taskModel);

        } else {
            throw new RuntimeException("User not found");
        }

        TaskResponse taskResponse = new TaskResponse();

        taskResponse.setIdTask(taskModel.getIdTask());
        taskResponse.setName(taskModel.getName());
        taskResponse.setDescription(taskModel.getDescription());
        taskResponse.setCreationDate(taskModel.getCreationDate());
        taskResponse.setCompleted(taskModel.getCompleted());
        taskResponse.setUser(taskModel.getUser().getIdUser());

        return taskResponse;
    }

    @Override
    @Cacheable("getUserTasks")
    public List<TaskResponse> getUserTasks(String username) {
        Optional<UserModel> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            List<TaskModel> taskModels = userOptional.get().getTasks();
            List<TaskResponse> completedTasks = new ArrayList<>();

            for (TaskModel taskModel : taskModels) {
                if (!taskModel.getCompleted()) {
                    TaskResponse taskResponse = new TaskResponse();
                    taskResponse.setIdTask(taskModel.getIdTask());
                    taskResponse.setName(taskModel.getName());
                    taskResponse.setDescription(taskModel.getDescription());
                    taskResponse.setCreationDate(taskModel.getCreationDate());
                    taskResponse.setCompleted(taskModel.getCompleted());
                    taskResponse.setUser(taskModel.getUser().getIdUser());
                    completedTasks.add(taskResponse);
                }
            }
            return completedTasks;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    @CacheEvict(cacheNames = {"getUserTasks", "generateExcelReport"}, allEntries = true)
    public boolean updateTaskById(Integer idTask) {
        Optional<TaskModel> optionalTaskModel = taskRepository.findById(idTask);
        if (optionalTaskModel.isPresent()) {
            TaskModel taskModel = optionalTaskModel.get();
            taskModel.setCompleted(true);
            taskRepository.save(taskModel);
            return true;
        } else {
            return false;
        }
    }

    @Override
    @CacheEvict(cacheNames = {"getUserTasks", "generateExcelReport"}, allEntries = true)
    public boolean deleteTaskById(Integer idTask) {
        Optional<TaskModel> taskModelOptional = taskRepository.findById(idTask);
        if (taskModelOptional.isPresent()) {
            taskRepository.deleteById(idTask);
            return true;
        }
        return false;
    }

    @Override
    @Cacheable("generateExcelReport")
    public byte[] generateExcelReport(String username) {
        List<ReportDataResponse> dataResponseList = new ArrayList<>();
        Optional<UserModel> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            List<TaskModel> taskModelList = userOptional.get().getTasks();

            for (TaskModel taskModel : taskModelList) {
                ReportDataResponse dataResponse = new ReportDataResponse();
                dataResponse.setIdTask(taskModel.getIdTask());
                dataResponse.setName(taskModel.getName());
                dataResponse.setDescription(taskModel.getDescription());
                dataResponse.setCreationDate(taskModel.getCreationDate());
                dataResponse.setCompleted(taskModel.getCompleted());
                dataResponseList.add(dataResponse);
            }
        } else {
            // En caso de que no se encuentre ningún usuario con el nombre proporcionado, lanza una excepción
            throw new RuntimeException("Data not found");
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Report");

            // Crear encabezados
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Nombre", "Descripción", "Fecha de Creación", "Status"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Escribir datos en el archivo
            int rowNum = 1;
            for (ReportDataResponse rowData : dataResponseList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(rowData.getName());
                row.createCell(1).setCellValue(rowData.getDescription());
                row.createCell(2).setCellValue(rowData.getCreationDate().toString());

                // Establecer el valor "Completada" o "No completada" según la tarea esté completada o no
                Cell completedCell = row.createCell(3);
                if (rowData.getCompleted()) {
                    completedCell.setCellValue("Completada");
                } else {
                    completedCell.setCellValue("No completada");
                }
            }

            // Guardar el archivo en un arreglo de bytes
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                workbook.write(outputStream);
                return outputStream.toByteArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    @Override
    public void sendNotification(String title, String body) {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();
        System.out.println("Enviando notificación:");
        System.out.println("  Título: " + title);
        System.out.println("  Cuerpo: " + body);

        Message message = Message.builder()
                .setNotification(notification)
                .setTopic("topic")
                .build();
        try {
            firebaseMessaging.send(message);
            System.out.println("Notificación enviada correctamente");
        } catch (FirebaseMessagingException e) {
            System.err.println("Error al enviar la notificación: " + e.getMessage());
        }
    }
}
