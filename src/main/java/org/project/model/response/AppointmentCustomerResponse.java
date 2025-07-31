package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentCustomerResponse {
    private Long id;
    private String patientName;
    private String relationship;
    private String doctorName;
    private String serviceName;
    private String startTime;
    private String status;
    private String resultUrl;
}
