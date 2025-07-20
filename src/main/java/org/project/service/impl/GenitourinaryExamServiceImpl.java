package org.project.service.impl;

import org.project.converter.CardiacConverter;
import org.project.converter.GenitourinaryConverter;
import org.project.entity.CardiacExamEntity;
import org.project.entity.GastrointestinalExam;
import org.project.entity.GenitourinaryExam;
import org.project.entity.MedicalRecordEntity;
import org.project.model.request.AddGenitourinaryRequest;
import org.project.model.response.GenitourinaryResponse;
import org.project.repository.CardiacExamRepository;
import org.project.repository.GastrointestinalExamRepository;
import org.project.repository.GenitourinaryExamRepository;
import org.project.repository.MedicalRecordRepository;
import org.project.service.GenitourinaryExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class GenitourinaryExamServiceImpl implements GenitourinaryExamService {
    @Autowired
    private GenitourinaryExamRepository genitourinaryExamRepository;
    @Autowired
    private MedicalRecordRepository medicalRecordRepository;
    @Autowired
    private GenitourinaryConverter genitourinaryConverter;
    @Override
    public boolean addGenitourinary(Long medicalRecordId, AddGenitourinaryRequest addGenitourinaryRequest) {
        MedicalRecordEntity medicalRecord = medicalRecordRepository.findById(medicalRecordId).orElse(null);
        if(medicalRecord != null) {
            GenitourinaryExam genitourinaryExam = GenitourinaryExam.builder()
                    .medicalRecord(medicalRecord)
                    .bladder(addGenitourinaryRequest.getBladder())
                    .genitalInspection(addGenitourinaryRequest.getGenitalInspection())
                    .kidneyArea(addGenitourinaryRequest.getKidneyArea())
                    .recordedAt(new Timestamp(System.currentTimeMillis()))
                    .build();
            genitourinaryExamRepository.save(genitourinaryExam);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateGenitourinary(Long respiratoryId, AddGenitourinaryRequest addGenitourinaryRequest) {
        GenitourinaryExam genitourinaryExam = genitourinaryExamRepository.findById(respiratoryId).orElse(null);
        if(genitourinaryExam != null) {
            genitourinaryExam.setBladder(addGenitourinaryRequest.getBladder());
            genitourinaryExam.setKidneyArea(addGenitourinaryRequest.getKidneyArea());
            genitourinaryExam.setGenitalInspection(addGenitourinaryRequest.getGenitalInspection());
            genitourinaryExam.setRecordedAt(new Timestamp(System.currentTimeMillis()));
            genitourinaryExamRepository.save(genitourinaryExam);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteGenitourinary(Long respiratoryId) {
        GenitourinaryExam genitourinaryExam = genitourinaryExamRepository.findById(respiratoryId).orElse(null);
        if(genitourinaryExam != null) {
            genitourinaryExamRepository.delete(genitourinaryExam);
            return true;
        }
        return false;
    }

    @Override
    public List<GenitourinaryResponse> getGenitourinary(Long medicalRecordId) {
        List<GenitourinaryExam> genitourinaryExams = genitourinaryExamRepository.findByMedicalRecordId(medicalRecordId);
        return genitourinaryExams.stream().map(genitourinaryConverter::toGenitourinaryResponse).toList();
    }
}
