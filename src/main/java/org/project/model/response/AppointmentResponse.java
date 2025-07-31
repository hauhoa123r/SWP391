package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {
    private Long id;
    private DoctorResponse doctorEntity;
    private PatientResponse patientEntity;
    private ServiceResponse serviceEntity;
    private Timestamp startTime;
    private Integer durationMinutes;
    private SchedulingCoordinatorResponse schedulingCoordinatorEntity;
    private String appointmentStatus;
}
