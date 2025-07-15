package org.project.service.impl;

import org.project.converter.MedicalRecordSymptomConverter;
import org.project.entity.MedicalRecordEntity;
import org.project.entity.MedicalRecordSymptomEntity;
import org.project.model.request.MedicalRecordSymptomRequest;
import org.project.model.response.MedicalRecordSymptomResponse;
import org.project.repository.MedicalRecordRepository;
import org.project.repository.MedicalRecordSymptomRepository;
import org.project.service.MedicalRecordSymptomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class MedicalRecordSymptomServiceImpl implements MedicalRecordSymptomService {
    @Autowired
    private MedicalRecordSymptomRepository medicalRecordSymptomRepository;
    @Autowired
    MedicalRecordRepository medicalRecordRepository;
    @Autowired
    MedicalRecordSymptomConverter medicalRecordSymptomConverter;

    public Long addMedicalRecordSymptom(Long medicalRecordId, MedicalRecordSymptomRequest symptom) {
        MedicalRecordEntity medicalRecord = medicalRecordRepository.findById(medicalRecordId).
                orElseThrow(() -> new IllegalArgumentException("Medical Record Not Found"));
        MedicalRecordSymptomEntity symptomEntity = new MedicalRecordSymptomEntity();
        symptomEntity.setMedicalRecordEntity(medicalRecord);
        symptomEntity.setDescription(symptom.getDescription());
        symptomEntity.setSymptomName(symptom.getSymptomName());
        symptomEntity.setDuration(symptom.getDuration());
        symptomEntity.setSeverity(symptom.getSeverity());
        symptomEntity.setOnsetDate(LocalDate.now());
        medicalRecordSymptomRepository.save(symptomEntity);
        return symptomEntity.getId();
    }

    @Override
    public List<MedicalRecordSymptomResponse> getSymptoms(Long medicalRecordId) {
        List<MedicalRecordSymptomEntity> symptomEntities = medicalRecordSymptomRepository.findByMedicalRecordEntityId(medicalRecordId);
        return symptomEntities.stream().map(medicalRecordSymptomConverter::toMedicalRecordSymptomResponse).toList();
    }

    @Override
    public List<Long> addMedicalRecordSymptom(Long medicalRecordId, List<MedicalRecordSymptomRequest> symptoms) {
        List<Long> ids = new ArrayList<>();
        for (MedicalRecordSymptomRequest symptom : symptoms) {
            Long id = addMedicalRecordSymptom(medicalRecordId, symptom);
            ids.add(id);
        }
        return ids;
    }

    @Transactional
    @Override
    public void updateMedicalRecordSymptom(Long symptomId, MedicalRecordSymptomRequest request) {
        MedicalRecordSymptomEntity symptom = medicalRecordSymptomRepository.findById(symptomId).orElseThrow(() -> new IllegalArgumentException("Symptom not found with ID: " + symptomId));
        symptom.setSymptomName(request.getSymptomName());
        symptom.setDuration(request.getDuration());
        symptom.setSeverity(request.getSeverity());
        symptom.setDescription(request.getDescription());
        symptom.setOnsetDate(LocalDate.now()); // Update onset date
    }
    @Transactional
    @Override
    public void deleteMedicalRecordSymptom(Long symptomId) {
        if (!medicalRecordSymptomRepository.existsById(symptomId)) {
            throw new IllegalArgumentException("Symptom not found with ID: " + symptomId);
        }
        medicalRecordSymptomRepository.deleteById(symptomId);
    }
}
