package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TopThreeAppointmentNearest {
    private Long appointmentId;
    private String name;
    private String timeAppointment;
    private String departmentName;
}
