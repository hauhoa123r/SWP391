package org.project.service.impl;

import org.project.converter.CardiacConverter;
import org.project.converter.MusculoskeletalConverter;
import org.project.entity.CardiacExamEntity;
import org.project.entity.MedicalRecordEntity;
import org.project.entity.MusculoskeletalExamEntity;
import org.project.model.request.AddMusculoskeletalRequest;
import org.project.model.response.MusculoskeletalResponse;
import org.project.repository.CardiacExamRepository;
import org.project.repository.MedicalRecordRepository;
import org.project.repository.MusculoskeletalExamRepository;
import org.project.service.MusculoskeletalExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MusculoskeletalExamServiceImpl implements MusculoskeletalExamService {
    @Autowired
    private MusculoskeletalExamRepository musculoskeletalExamRepository ;
    @Autowired
    private MedicalRecordRepository medicalRecordRepository;
    @Autowired
    private MusculoskeletalConverter musculoskeletalConverter;
    @Override
    public boolean addMusculoskeletal(Long medicalRecordId, AddMusculoskeletalRequest addMusculoskeletalRequest) {
        MedicalRecordEntity medicalRecord = medicalRecordRepository.findById(medicalRecordId).orElse(null);
        if(medicalRecord != null) {
            MusculoskeletalExamEntity musculoskeletalExamEntity = MusculoskeletalExamEntity.builder()
                    .medicalRecord(medicalRecord)
                    .jointExam(addMusculoskeletalRequest.getJointExam())
                    .muscleStrength(addMusculoskeletalRequest.getMuscleStrength())
                    .deformity(addMusculoskeletalRequest.getDeformity())
                    .recordedAt(new Timestamp(System.currentTimeMillis()))
                    .build();
            musculoskeletalExamRepository.save(musculoskeletalExamEntity);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateMusculoskeletal(Long musculoskeletalId, AddMusculoskeletalRequest addMusculoskeletalRequest) {
        MusculoskeletalExamEntity musculoskeletalExamEntity = musculoskeletalExamRepository.findById(musculoskeletalId).orElse(null);
        if(musculoskeletalExamEntity != null) {
            musculoskeletalExamEntity.setJointExam(addMusculoskeletalRequest.getJointExam());
            musculoskeletalExamEntity.setDeformity(addMusculoskeletalRequest.getDeformity());
            musculoskeletalExamEntity.setMuscleStrength(addMusculoskeletalRequest.getMuscleStrength());
            musculoskeletalExamEntity.setRecordedAt(new Timestamp(System.currentTimeMillis()));
            musculoskeletalExamRepository.save(musculoskeletalExamEntity);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteMusculoskeletal(Long musculoskeletalId) {
        MusculoskeletalExamEntity musculoskeletalExamEntity = musculoskeletalExamRepository.findById(musculoskeletalId).orElse(null);
        if(musculoskeletalExamEntity != null) {
            musculoskeletalExamRepository.delete(musculoskeletalExamEntity);
            return true;
        }
        return false;
    }

    @Override
    public List<MusculoskeletalResponse> getMusculoskeletal(Long medicalRecordId) {
        List<MusculoskeletalExamEntity> musculoskeletalExamEntities = musculoskeletalExamRepository.findByMedicalRecordId(medicalRecordId);
        return musculoskeletalExamEntities.stream().map(musculoskeletalConverter::toMusculoskeletalResponse).toList();
    }
}
