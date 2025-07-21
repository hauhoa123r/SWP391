package org.project.service;

import org.project.entity.UserEntity;
import org.project.entity.UserEntity;
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

    PatientResponse getPatientById(Long patientId);

    PatientResponse updatePatient(Long patientId, PatientDTO patientDTO);

    void deletePatient(Long patientId);

    List<String> getAllRelationships(Long userId);

    Long getPatientIdByUserId(Long userId);

    String toConvertAvatarUrl(String avatarBase64);

    String toConvertFileToBase64(String avatarUrl);

    UserEntity getUserHasPatient();

    boolean isPatientExist(Long patientId);

    boolean isPatientExistByIdentityNumber(String identityNumber);
}
