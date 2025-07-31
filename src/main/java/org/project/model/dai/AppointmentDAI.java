package org.project.model.dai;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDAI {
    private Long userId;
    private String time;
    private String date;
    private Long doctorId;
    private String doctorName;
    private Long departmentId;
    private Long hospitalId;
    private String departmentName;
    private String hospitalName;
    private String patientName;
    private Boolean isFormStarted;
    private String language;
}
