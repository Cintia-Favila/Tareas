package com.example.demo.request;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TaskRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private String creationDate;
}
