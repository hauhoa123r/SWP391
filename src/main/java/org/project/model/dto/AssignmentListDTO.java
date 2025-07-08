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
public class AssignmentListDTO extends AbstractServiceAIDTO{
    private Long id;
    private String patientName;
    private String departmentName;
    private String doctorName;
    private String testType;
    private Date requestAt;
    private String status;
    private String reason;
}
