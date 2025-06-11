package org.project.model.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.project.enums.AppointmentStatus;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppointmentDTO {
    Long id;
    AppointmentStatus appointmentStatus;
}
