package org.project.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.project.enums.AppointmentStatus;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppointmentListResponse {
    Long id;
    String patientEntityName;
    String serviceEntityProductEntityName;
    Timestamp startTime;
    Integer durationMinutes;
    String schedulingCoordinatorEntityName;
    AppointmentStatus appointmentStatus;
    public String getAppointmentStatus() {
        return appointmentStatus != null ? appointmentStatus.getValue() : null;
    }
}
