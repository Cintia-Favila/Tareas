package com.example.demo.response;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class TaskResponse implements Serializable {
    private Integer idTask;
    private String name;
    private String description;
    private LocalDate creationDate;
    private Boolean completed;
    private Integer user;
}
