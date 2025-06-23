package org.project.service;

import org.project.model.dto.PatientDTO;
import org.project.model.response.PatientResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PatientService {
    Page<PatientResponse> getPatientsByUser(Long userId, int index, int size);

    Page<PatientResponse> getPatientsByUserAndKeyword(Long userId, String keyword, int index, int size);

    Long createPatient(PatientDTO patientDTO);

    List<PatientResponse> getAllPatients();

    List<PatientResponse> getAllPatientsByUserId(Long userId);

    Page<PatientResponse> getAllPatientsByUserIdForPage(Long userId, int page, int size);

    PatientResponse getPatientById(Long patientId);

    void updatePatient(Long patientId, PatientDTO patientDTO);

    void deletePatient(Long patientId);

    List<String> getAllRelationships(Long userId);

    Long getPatientIdByUserId(Long userId);
}
