package org.project.repository.impl;

import org.project.entity.TestRequestEntity;
import org.project.model.dto.AssignmentListDTO;
import org.springframework.data.domain.Page;

public interface AssignmentRepositoryCustom {
    Page<TestRequestEntity> toGetEntityBySearch(AssignmentListDTO assignmentListDTO) throws IllegalAccessException;
}
