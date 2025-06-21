package org.project.service;

import org.project.model.response.PatientResponse;
import org.springframework.data.domain.Page;

public interface PatientService {
//    void createPatient(PatientDTO patientDTO);
//
//    List<PatientResponse> getAllPatients();
//
//    Optional<PatientResponse> getPatientById(Long patientId);
//
//    void updatePatient(Long patientId, PatientDTO patientDTO);
//
//    void deletePatient(Long patientId);

    Page<PatientResponse> getPatientsByUser(Long userId, int index, int size);

    Page<PatientResponse> getPatientsByUserAndKeyword(Long userId, String keyword, int index, int size);
}
