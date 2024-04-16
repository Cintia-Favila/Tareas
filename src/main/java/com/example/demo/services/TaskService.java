package com.example.demo.services;
import com.example.demo.request.TaskRequest;
import com.example.demo.response.ReportDataResponse;
import com.example.demo.response.TaskResponse;

import java.util.List;

public interface TaskService {

    TaskResponse createTask(TaskRequest taskRequest);

    boolean updateTaskById(Integer idTask);

    boolean deleteTaskById(Integer idTask);

    List<TaskResponse> getUserTasks(String username);

    void sendNotification(String title, String body);

    byte[] generateExcelReport(String username);
}
