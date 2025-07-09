package org.project.service;

import org.project.model.dto.AssignmentListDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AssignmentService {
    Page<AssignmentListDTO> getAssignmentBySearch(AssignmentListDTO search) throws IllegalAccessException;

    boolean receivePatient(Long id);

    boolean reveicePatientByMultileChoise(List<Long> ids);
}
