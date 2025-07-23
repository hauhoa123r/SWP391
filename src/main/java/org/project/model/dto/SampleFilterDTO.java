package org.project.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.project.enums.RequestStatus;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SampleFilterDTO extends AbstractServiceDTO {
    private String patientName;
    private String testType;
    private RequestStatus status;
}
