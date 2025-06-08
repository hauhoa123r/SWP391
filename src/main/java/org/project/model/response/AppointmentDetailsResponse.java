package org.project.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.project.entity.PatientsEntity;
import org.project.enums.AppointmentStatus;

import java.security.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppointmentDetailsResponse {
    Long id;
    PatientsEntity patient;
    AppointmentStatus status;
    Timestamp date;
}
