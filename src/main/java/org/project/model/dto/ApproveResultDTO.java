package org.project.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApproveResultDTO extends AbstractServiceDTO{
    private String patientName;
    private String testType;
    private String TesterName;
    private String status;
}
