package org.project.repository.impl;

import org.project.entity.TestRequestEntity;
import org.project.model.dto.AssignmentListDTO;
import org.springframework.data.domain.Page;

public interface ReceiveRepositoryCustom {
    Page<TestRequestEntity> toGetReceivePatientByStatusPending(AssignmentListDTO assignmentListDTO) throws IllegalAccessException;
}
