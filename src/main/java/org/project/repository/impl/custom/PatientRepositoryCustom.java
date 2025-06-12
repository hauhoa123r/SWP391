package org.project.repository.impl.custom;

import java.util.List;

public interface PatientRepositoryCustom {
    List<String> getAllRelationships(Long userId);
    Long getPatientIdByUserId(Long userId);
}
