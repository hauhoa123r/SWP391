package org.project.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TreatmentPatientDTO {
    private Long appointmentId;
    private String startDate;
    private String endDate;
    private String diagnosis;
    private String treatmentMethod;
}
