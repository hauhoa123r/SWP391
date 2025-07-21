package org.project.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {
    private Long id;
    private Long doctorEntityId;
    private Long patientEntityUserEntityId;
    private Long patientEntityId;
    private Long serviceEntityId;
    private Timestamp startTime;
    private Integer durationMinutes;
    private Long schedulingCoordinatorEntityId;
}