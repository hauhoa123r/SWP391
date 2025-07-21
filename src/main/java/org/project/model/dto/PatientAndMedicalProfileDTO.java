package org.project.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatientAndMedicalProfileDTO {
    private PatientDTO patientDTO;
    private MedicalProfileDTO medicalProfileDTO;
}
