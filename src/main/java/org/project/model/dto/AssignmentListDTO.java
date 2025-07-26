package org.project.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentListDTO extends AbstractServiceDTO {
    private Long id;
    private String patientName;
    private String departmentName;
    private String doctorName;
    private String testType;
    private LocalDate requestAt;
    private String status;
    private String reason;
}
