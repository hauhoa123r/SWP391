package org.project.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.project.converter.MedicalProfileConverter;
import org.project.entity.MedicalProfileEntity;
import org.project.model.response.MedicalProfileVResponse;
import org.project.repository.MedicalProfilesRepository;
import org.project.service.MedicalProfilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicalProfilesServiceImpl implements MedicalProfilesService {
    @Autowired
    private MedicalProfilesRepository medicalProfileRepository;
    @Autowired
    private MedicalProfileConverter medicalProfileConverter;

    @Override
    public MedicalProfileVResponse getMedicalProfileOfPatient(Long patientId) {
        MedicalProfileEntity medicalProfileEntity = medicalProfileRepository.findByPatientEntityId(patientId);
        if (medicalProfileEntity == null) {
            throw new RuntimeException("Medical profile not found for patient ID: " + patientId);
        }
        return medicalProfileConverter.toMedicalProfileResponse(medicalProfileEntity);
    }

    @Override
    public MedicalProfileVResponse updateAllergiesAndChronicDiseases(Long patientId, List<String> allergies, List<String> chronicDiseases) {
        MedicalProfileEntity entity = medicalProfileRepository.findByPatientEntityId(patientId);
        if (entity == null) {
            throw new RuntimeException("Medical profile not found for patient ID: " + patientId);
        }
        try {
            entity.setAllergies(new ObjectMapper().writeValueAsString(allergies));
            entity.setChronicDiseases(new ObjectMapper().writeValueAsString(chronicDiseases));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert lists to JSON", e);
        }
        medicalProfileRepository.save(entity);
        return medicalProfileConverter.toMedicalProfileResponse(entity);
    }
}
