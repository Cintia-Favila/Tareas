package com.example.demo.response;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ReportDataResponse {
    private Integer idTask;
    private String name;
    private String description;
    private LocalDate creationDate;
    private Boolean completed;
}
