package org.project.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentListDTO {
    private String patientName;
    private String departmentName;
    private String testType;
    private Date requestAt;
    private String status;
    private String reason;
    private int page = 0;
    private int size = 10;
}
