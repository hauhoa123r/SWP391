package org.project.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.project.enums.AppointmentStatus;

import java.security.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppointmentsResponse {
    Long id;
    String patientName;
    String coordinatorName;
    AppointmentStatus status;
    Timestamp date;
}
