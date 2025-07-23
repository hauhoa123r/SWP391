package org.project.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.project.enums.AppointmentStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeAppointmentDTO {
    private Long appointmentId;
    private Long staffScheduleId;
    private Long doctorId;
    private String appointmentDate;
    private AppointmentStatus appointmentStatus = AppointmentStatus.CONFIRMED;
}
