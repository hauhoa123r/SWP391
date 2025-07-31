package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentFilterResponse {
    private Long appointmentId;
    private String patientName;
    private String departmentName;
    private String timeAppointment;
    private String resultURL;
    private String gender;
    private String age;
    private String phone;
    private String date;
}
