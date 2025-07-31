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
public class AppointmentDTO extends SortBaseDTO {
    private Long id;
    private Long doctorEntityId;
    private String doctorEntityStaffEntityFullName;
    private Long patientEntityUserEntityId;
    private Long patientEntityId;
    private String patientEntityFullName;
    private String patientEntityEmail;
    private String serviceEntityProductEntityName;
    private Long serviceEntityId;
    private Timestamp startTime;
    private Integer durationMinutes;
    private Long schedulingCoordinatorEntityId;
    private String schedulingCoordinatorEntityStaffEntityFullName;
    private String appointmentStatus;
}