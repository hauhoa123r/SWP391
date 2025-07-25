package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.project.model.dto.PatientDTO;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MakeAppointmentResponse {
    private int hospitalId;
    private String hospitalName;
    private String departmentName;
    private String doctorName;
    private String startDate;
    private PatientDTO patient;
}
