package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorHomepageResponse {
    private String doctorName;
    private Long totalAppointmentsToday;
    private Long totalPatients;
    private List<TopThreeAppointmentNearest> topThreeAppointmentNearest;
}
