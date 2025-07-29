package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentDashboardCustomerResponse {
    private Long id;
    private String patientName;
    private String date;
    private String time;
    private String doctorName;
    private String status;
}
