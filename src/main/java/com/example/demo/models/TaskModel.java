package com.example.demo.models;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "tasks")
public class TaskModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idTask;
    private String name;
    private String description;
    @Column(name = "creation_date")
    private LocalDate creationDate;
    @Column(name = "Task completed")
    private Boolean completed;

    @ManyToOne(targetEntity = UserModel.class)
    private UserModel user;
}
