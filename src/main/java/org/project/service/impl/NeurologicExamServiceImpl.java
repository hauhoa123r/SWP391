package org.project.service.impl;

import org.project.converter.CardiacConverter;
import org.project.converter.NeurologicConverter;
import org.project.entity.CardiacExamEntity;
import org.project.entity.MedicalRecordEntity;
import org.project.entity.NeurologicExamEntity;
import org.project.model.request.AddNeurologicRequest;
import org.project.model.response.NeurologicResponse;
import org.project.repository.CardiacExamRepository;
import org.project.repository.MedicalRecordRepository;
import org.project.repository.NeurologicExamRepository;
import org.project.service.NeurologicExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class NeurologicExamServiceImpl implements NeurologicExamService {
    @Autowired
    private NeurologicExamRepository neurologicExamRepository;
    @Autowired
    private MedicalRecordRepository medicalRecordRepository;
    @Autowired
    private NeurologicConverter neurologicConverter;


    @Override
    public boolean addNeurologic(Long medicalRecordId, AddNeurologicRequest addNeurologicRequest) {
        MedicalRecordEntity medicalRecord = medicalRecordRepository.findById(medicalRecordId).orElse(null);
        if(medicalRecord != null) {
            NeurologicExamEntity neurologicExamEntity = NeurologicExamEntity.builder()
                    .medicalRecord(medicalRecord)
                    .cranialNerves(addNeurologicRequest.getCranialNerves())
                    .consciousness(addNeurologicRequest.getConsciousness())
                    .motorFunction(addNeurologicRequest.getMotorFunction())
                    .sensoryFunction(addNeurologicRequest.getSensoryFunction())
                    .reflexes(addNeurologicRequest.getReflexes())
                    .recordedAt(new Timestamp(System.currentTimeMillis()))
                    .build();
            neurologicExamRepository.save(neurologicExamEntity);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateNeurologic(Long neurologicId, AddNeurologicRequest addNeurologicRequest) {
        NeurologicExamEntity neurologicExamEntity = neurologicExamRepository.findById(neurologicId).orElse(null);
        if(neurologicExamEntity != null) {
            neurologicExamEntity.setConsciousness(addNeurologicRequest.getConsciousness());
            neurologicExamEntity.setMotorFunction(addNeurologicRequest.getMotorFunction());
            neurologicExamEntity.setCranialNerves(addNeurologicRequest.getCranialNerves());
            neurologicExamEntity.setSensoryFunction(addNeurologicRequest.getSensoryFunction());
            neurologicExamEntity.setReflexes(addNeurologicRequest.getReflexes());
            neurologicExamEntity.setRecordedAt(new Timestamp(System.currentTimeMillis()));
             neurologicExamRepository.save(neurologicExamEntity);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteNeurologic(Long neurologicId) {
        NeurologicExamEntity neurologicExamEntity = neurologicExamRepository.findById(neurologicId).orElse(null);
        if(neurologicExamEntity != null) {
            neurologicExamRepository.delete(neurologicExamEntity);
            return true;
        }
        return false;
    }

    @Override
    public List<NeurologicResponse> getNeurologic(Long medicalRecordId) {
        List<NeurologicExamEntity> neurologicExamEntities = neurologicExamRepository.findByMedicalRecordId(medicalRecordId);
        return neurologicExamEntities.stream().map(neurologicConverter::toNeurologicResponse).toList();
    }
}
