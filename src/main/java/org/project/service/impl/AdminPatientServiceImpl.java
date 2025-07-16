package org.project.service.impl;

import lombok.RequiredArgsConstructor;
import org.project.entity.PatientEntity;
import org.project.exception.sql.EntityNotFoundException;
import org.project.mapper.AdminPatientMapper;
import org.project.model.request.AdminPatientUpdateRequest;
import org.project.model.response.AdminPatientDetailResponse;
import org.project.model.response.AdminPatientResponse;
import org.project.repository.AdminPatientRepository;
import org.project.repository.UserRepository;
import org.project.service.AdminPatientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminPatientServiceImpl implements AdminPatientService {

    private final AdminPatientRepository patientRepository;
    private final AdminPatientMapper patientMapper;
    private final UserRepository userRepository;

    @Override
    public Page<AdminPatientResponse> getAllPatients(Pageable pageable) {
        return patientRepository.findAll(pageable).map(patientMapper::toResponse);
    }

    @Override
    public Page<AdminPatientResponse> getAllPatients(Pageable pageable, String keyword, String field) {
        Page<PatientEntity> patientPage;

        if (keyword == null || keyword.trim().isEmpty()) {
            patientPage = patientRepository.findAll(pageable);
        } else {
            switch (field == null ? "name" : field.toLowerCase()) {
                case "email" -> patientPage = patientRepository.findByEmailContainingIgnoreCase(keyword, pageable);
                case "phone" -> patientPage = patientRepository.findByPhoneNumberContainingIgnoreCase(keyword, pageable);
                case "address" -> patientPage = patientRepository.findByAddressContainingIgnoreCase(keyword, pageable);
                default -> patientPage = patientRepository.findByFullNameContainingIgnoreCase(keyword, pageable); // name
            }
        }

        return patientPage.map(patientMapper::toResponse);
    }

    @Override
    public AdminPatientDetailResponse getPatientDetail(Long id) {
        PatientEntity patient = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));
        return patientMapper.toDetailResponse(patient);
    }

    @Override
    public PatientEntity getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));
    }

    @Override
    public AdminPatientUpdateRequest getUpdateForm(Long id) {
        PatientEntity patient = getPatientById(id);
        return patientMapper.toUpdateRequest(patient);
    }

    @Override
    public void updatePatient(Long id, AdminPatientUpdateRequest request) {
        PatientEntity patient = getPatientById(id);
        patientMapper.updatePatientFromRequest(request, patient);
        patient.setUserEntity(userRepository.findById(request.getUserId()).orElseThrow());
        patientRepository.save(patient);
    }
}