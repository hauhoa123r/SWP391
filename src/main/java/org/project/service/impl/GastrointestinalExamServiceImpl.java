package org.project.service.impl;

import org.project.converter.CardiacConverter;
import org.project.converter.GastrointestinalConverter;
import org.project.entity.CardiacExamEntity;
import org.project.entity.GastrointestinalExam;
import org.project.entity.MedicalRecordEntity;
import org.project.model.request.AddGastrointestinalRequest;
import org.project.model.response.GastrointestinalResponse;
import org.project.repository.CardiacExamRepository;
import org.project.repository.GastrointestinalExamRepository;
import org.project.repository.MedicalRecordRepository;
import org.project.service.GastrointestinalExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class GastrointestinalExamServiceImpl implements GastrointestinalExamService {
    @Autowired
    private GastrointestinalExamRepository gastrointestinalExamRepository;
    @Autowired
    private MedicalRecordRepository medicalRecordRepository;
    @Autowired
    private GastrointestinalConverter gastrointestinalConverter;
    @Override
    public boolean addGastrointestinal(Long medicalRecordId, AddGastrointestinalRequest addGastrointestinalRequest) {
        MedicalRecordEntity medicalRecord = medicalRecordRepository.findById(medicalRecordId).orElse(null);
        if(medicalRecord != null) {
            GastrointestinalExam gastrointestinalExam = GastrointestinalExam.builder()
                    .medicalRecord(medicalRecord)
                    .abdominalInspection(addGastrointestinalRequest.getAbdominalInspection())
                    .palpation(addGastrointestinalRequest.getPalpation())
                    .percussion(addGastrointestinalRequest.getPercussion())
                    .auscultation(addGastrointestinalRequest.getAuscultation())
                    .recordedAt(new Timestamp(System.currentTimeMillis()))
                    .build();
            gastrointestinalExamRepository.save(gastrointestinalExam);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateGastrointestinal(Long gastrointestinalId, AddGastrointestinalRequest addGastrointestinalRequest) {
        GastrointestinalExam gastrointestinalExam = gastrointestinalExamRepository.findById(gastrointestinalId).orElse(null);
        if(gastrointestinalExam != null) {
            gastrointestinalExam.setAbdominalInspection(addGastrointestinalRequest.getAbdominalInspection());
            gastrointestinalExam.setPalpation(addGastrointestinalRequest.getPalpation());
            gastrointestinalExam.setPercussion(addGastrointestinalRequest.getPercussion());
            gastrointestinalExam.setAuscultation(addGastrointestinalRequest.getAuscultation());
            gastrointestinalExam.setRecordedAt(new Timestamp(System.currentTimeMillis()));
            gastrointestinalExamRepository.save(gastrointestinalExam);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteGastrointestinal(Long gastrointestinalId) {
        GastrointestinalExam gastrointestinalExam = gastrointestinalExamRepository.findById(gastrointestinalId).orElse(null);
        if(gastrointestinalExam != null) {
            gastrointestinalExamRepository.delete(gastrointestinalExam);
            return true;
        }
        return false;
    }

    @Override
    public List<GastrointestinalResponse> getGastrointestinal(Long medicalRecordId) {
        List<GastrointestinalExam> gastrointestinalExams = gastrointestinalExamRepository.findByMedicalRecordId(medicalRecordId);
        return gastrointestinalExams.stream().map(gastrointestinalConverter::toGastrointestinalResponse).toList();
    }
}
