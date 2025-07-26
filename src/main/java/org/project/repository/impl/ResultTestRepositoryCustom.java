package org.project.repository.impl;

import org.project.entity.AppointmentEntity;
import org.project.model.dto.ResultTestDTO;
import org.springframework.data.domain.Page;

public interface ResultTestRepositoryCustom {
    Page<AppointmentEntity> filterAppointmentEntityCustom(ResultTestDTO resultTestDTO) throws IllegalAccessException;
}
