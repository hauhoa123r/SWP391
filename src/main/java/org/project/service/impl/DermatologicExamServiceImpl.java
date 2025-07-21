package org.project.service.impl;

import org.project.converter.CardiacConverter;
import org.project.converter.DermatologicConverter;
import org.project.entity.CardiacExamEntity;
import org.project.entity.DermatologicExamEntity;
import org.project.entity.MedicalRecordEntity;
import org.project.model.request.AddDermatologicRequest;
import org.project.model.response.DermatologicResponse;
import org.project.repository.CardiacExamRepository;
import org.project.repository.DermatologicExamRepository;
import org.project.repository.MedicalRecordRepository;
import org.project.service.DermatologicExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
@Service
public class DermatologicExamServiceImpl implements DermatologicExamService {
    @Autowired
    private DermatologicExamRepository dermatologicExamRepository;
    @Autowired
    private MedicalRecordRepository medicalRecordRepository;
    @Autowired
    private DermatologicConverter dermatologicConverter;

    @Override
    public boolean addDermatologic(Long medicalRecordId, AddDermatologicRequest addDermatologicRequest) {
        MedicalRecordEntity medicalRecord = medicalRecordRepository.findById(medicalRecordId).orElse(null);
        if(medicalRecord != null) {
            DermatologicExamEntity dermatologicExamEntity = DermatologicExamEntity.builder()
                    .medicalRecord(medicalRecord)
                    .lesions(addDermatologicRequest.getLesions())
                    .skinAppearance(addDermatologicRequest.getSkinAppearance())
                    .rash(addDermatologicRequest.getRash())
                    .recordedAt(new Timestamp(System.currentTimeMillis()))
                    .build();
            dermatologicExamRepository.save(dermatologicExamEntity);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateDermatologic(Long dermatologicId, AddDermatologicRequest addDermatologicRequest) {
        DermatologicExamEntity dermatologicExamEntity = dermatologicExamRepository.findById(dermatologicId).orElse(null);
        if(dermatologicExamEntity != null) {
            dermatologicExamEntity.setRash(addDermatologicRequest.getRash());
            dermatologicExamEntity.setSkinAppearance(addDermatologicRequest.getSkinAppearance());
            dermatologicExamEntity.setLesions(addDermatologicRequest.getLesions());
            dermatologicExamEntity.setRecordedAt(new Timestamp(System.currentTimeMillis()));
            dermatologicExamRepository.save(dermatologicExamEntity);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteDermatologic(Long dermatologicId) {
        DermatologicExamEntity dermatologicExamEntity = dermatologicExamRepository.findById(dermatologicId).orElse(null);
        if(dermatologicExamEntity != null) {
            dermatologicExamRepository.delete(dermatologicExamEntity);
            return true;
        }
        return false;
    }

    @Override
    public List<DermatologicResponse> getDermatologic(Long medicalRecordId) {
        List<DermatologicExamEntity> dermatologicExamEntities = dermatologicExamRepository.findByMedicalRecordId(medicalRecordId);
        return dermatologicExamEntities.stream().map(dermatologicConverter::toDermatologicResponse).toList();
    }
}
