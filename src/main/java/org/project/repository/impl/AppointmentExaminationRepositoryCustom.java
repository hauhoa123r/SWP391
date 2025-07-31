package org.project.repository.impl;

import org.project.entity.AppointmentEntity;
import org.project.model.dto.AppointmentFilterDTO;
import org.springframework.data.domain.Page;

public interface AppointmentExaminationRepositoryCustom {
    Page<AppointmentEntity> toFilterAppointmentByDoctorIdAndPatientName(AppointmentFilterDTO appointmentFilterDTO);
}
