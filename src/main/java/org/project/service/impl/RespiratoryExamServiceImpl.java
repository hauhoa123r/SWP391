package org.project.service.impl;

import org.project.converter.RespiratoryConverter;
import org.project.entity.MedicalRecordEntity;
import org.project.entity.RespiratoryExamEntity;
import org.project.model.request.AddRespiratoryRequest;
import org.project.model.response.RespiratoryResponse;
import org.project.repository.MedicalRecordRepository;
import org.project.repository.RespiratoryExamRepository;
import org.project.service.RespiratoryExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
@Service
public class RespiratoryExamServiceImpl implements RespiratoryExamService {
    @Autowired
    private RespiratoryExamRepository respiratoryExamRepository;
    @Autowired
    private MedicalRecordRepository medicalRecordRepository;
    @Autowired
    private RespiratoryConverter respiratoryConverter;
    @Override
    public boolean addRespiratory(Long medicalRecordId, AddRespiratoryRequest addRespiratoryRequest) {
        MedicalRecordEntity medicalRecord = medicalRecordRepository.findById(medicalRecordId).orElse(null);
        if(medicalRecord != null) {
            RespiratoryExamEntity respiratoryExamEntity = RespiratoryExamEntity.builder()
                    .medicalRecord(medicalRecord)
                    .auscultation(addRespiratoryRequest.getAuscultation())
                    .breathingPattern(addRespiratoryRequest.getBreathingPattern())
                    .fremitus(addRespiratoryRequest.getFremitus())
                    .percussionNote(addRespiratoryRequest.getPercussionNote())
                    .recordedAt(new Timestamp(System.currentTimeMillis()))
                    .build();
            respiratoryExamRepository.save(respiratoryExamEntity);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateRespiratory(Long respiratoryId, AddRespiratoryRequest addRespiratoryRequest) {
        RespiratoryExamEntity respiratoryExamEntity = respiratoryExamRepository.findById(respiratoryId).orElse(null);
        if(respiratoryExamEntity != null) {
            respiratoryExamEntity.setAuscultation(addRespiratoryRequest.getAuscultation());
            respiratoryExamEntity.setBreathingPattern(addRespiratoryRequest.getBreathingPattern());
            respiratoryExamEntity.setFremitus(addRespiratoryRequest.getFremitus());
            respiratoryExamEntity.setPercussionNote(addRespiratoryRequest.getPercussionNote());
            respiratoryExamEntity.setRecordedAt(new Timestamp(System.currentTimeMillis()));
            respiratoryExamRepository.save(respiratoryExamEntity);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteRespiratory(Long respiratoryId) {
        RespiratoryExamEntity respiratoryExamEntity = respiratoryExamRepository.findById(respiratoryId).orElse(null);
        if(respiratoryExamEntity != null) {
            respiratoryExamRepository.delete(respiratoryExamEntity);
            return true;
        }
        return false;
    }

    @Override
    public List<RespiratoryResponse> getRespiratory(Long medicalRecordId) {
        List<RespiratoryExamEntity> respiratoryExamEntities = respiratoryExamRepository.findByMedicalRecordId(medicalRecordId);
        return respiratoryExamEntities.stream().map(respiratoryConverter::toRespiratoryResponse).toList();
    }
}
