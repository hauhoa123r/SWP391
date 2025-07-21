package org.project.service.impl;

import org.project.converter.CardiacConverter;
import org.project.entity.CardiacExamEntity;
import org.project.entity.MedicalRecordEntity;
import org.project.entity.RespiratoryExamEntity;
import org.project.model.request.AddCardiac;
import org.project.model.response.CardiacResponse;
import org.project.model.response.RespiratoryResponse;
import org.project.repository.CardiacExamRepository;
import org.project.repository.MedicalRecordRepository;
import org.project.service.CardiacExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class CardiacExamServiceImpl implements CardiacExamService {
    @Autowired
    private CardiacExamRepository cardiacExamRepository;
    @Autowired
    private MedicalRecordRepository medicalRecordRepository;
    @Autowired
    private CardiacConverter cardiacConverter;

    @Override
    public boolean addCardiac(Long medicalRecordId, AddCardiac addCardiac) {
        MedicalRecordEntity medicalRecord = medicalRecordRepository.findById(medicalRecordId).orElse(null);
        if(medicalRecord != null) {
            CardiacExamEntity cardiacExamEntity = CardiacExamEntity.builder()
                    .medicalRecord(medicalRecord)
                    .edema(addCardiac.getEdema())
                    .heartSounds(addCardiac.getHeartSounds())
                    .murmur(addCardiac.getMurmur())
                    .jugularVenousPressure(addCardiac.getJugularVenousPressure())
                    .heartRate(addCardiac.getHeartRate())
                    .recordedAt(new Timestamp(System.currentTimeMillis()))
                    .build();
            cardiacExamRepository.save(cardiacExamEntity);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateCardiac(Long cardiacId, AddCardiac addCardiac) {
        CardiacExamEntity cardiacExamEntity = cardiacExamRepository.findById(cardiacId).orElse(null);
        if(cardiacExamEntity != null) {
            cardiacExamEntity.setEdema(addCardiac.getEdema());
            cardiacExamEntity.setHeartSounds(addCardiac.getHeartSounds());
            cardiacExamEntity.setMurmur(addCardiac.getMurmur());
            cardiacExamEntity.setHeartRate(addCardiac.getHeartRate());
            cardiacExamEntity.setJugularVenousPressure(addCardiac.getJugularVenousPressure());
            cardiacExamEntity.setRecordedAt(new Timestamp(System.currentTimeMillis()));
            cardiacExamRepository.save(cardiacExamEntity);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteCardiac(Long cardiacId) {
        CardiacExamEntity cardiacExamEntity = cardiacExamRepository.findById(cardiacId).orElse(null);
        if(cardiacExamEntity != null) {
            cardiacExamRepository.delete(cardiacExamEntity);
            return true;
        }
        return false;
    }

    @Override
    public List<CardiacResponse> getCardiac(Long medicalRecordId) {
        List<CardiacExamEntity> respiratoryExamEntities = cardiacExamRepository.findByMedicalRecordId(medicalRecordId);
        return respiratoryExamEntities.stream().map(cardiacConverter::toCardiacResponse).toList();
    }
}
