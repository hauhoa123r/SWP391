package org.project.service.impl;

import org.project.converter.CardiacConverter;
import org.project.converter.ClinicalNoteConverter;
import org.project.entity.CardiacExamEntity;
import org.project.entity.ClinicalNoteEntity;
import org.project.entity.MedicalRecordEntity;
import org.project.model.request.AddClinalNoteRequest;
import org.project.model.response.ClinalNoteResponse;
import org.project.repository.CardiacExamRepository;
import org.project.repository.ClinicalNoteRepository;
import org.project.repository.MedicalRecordRepository;
import org.project.service.ClinicalNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class ClinicalNoteServiceImpl implements ClinicalNoteService {
    @Autowired
    private ClinicalNoteRepository clinicalNoteRepository;
    @Autowired
    private MedicalRecordRepository medicalRecordRepository;
    @Autowired
    private ClinicalNoteConverter clinicalNoteConverter;
    @Override
    public boolean addClinicalNote(Long medicalRecordId, AddClinalNoteRequest addClinalNoteRequest) {
        MedicalRecordEntity medicalRecord = medicalRecordRepository.findById(medicalRecordId).orElse(null);
        if(medicalRecord != null) {
            ClinicalNoteEntity clinicalNoteEntity = ClinicalNoteEntity.builder()
                    .medicalRecord(medicalRecord)
                    .noteText(addClinalNoteRequest.getNoteText())
                    .recordedAt(new Timestamp(System.currentTimeMillis()))
                    .build();
            clinicalNoteRepository.save(clinicalNoteEntity);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateClinicalNote(Long clinicalNoteId, AddClinalNoteRequest addClinalNoteRequest) {
        ClinicalNoteEntity clinicalNoteEntity = clinicalNoteRepository.findById(clinicalNoteId).orElse(null);
        if(clinicalNoteEntity != null) {
            clinicalNoteEntity.setNoteText(addClinalNoteRequest.getNoteText());
            clinicalNoteEntity.setRecordedAt(new Timestamp(System.currentTimeMillis()));
            clinicalNoteRepository.save(clinicalNoteEntity);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteClinicalNote(Long clinicalNoteId) {
        ClinicalNoteEntity clinicalNoteEntity = clinicalNoteRepository.findById(clinicalNoteId).orElse(null);
        if(clinicalNoteEntity != null) {
            clinicalNoteRepository.delete(clinicalNoteEntity);
            return true;
        }
        return false;
    }

    @Override
    public List<ClinalNoteResponse> getClinicalNote(Long medicalRecordId) {
        List<ClinicalNoteEntity> clinicalNoteEntities = clinicalNoteRepository.findByMedicalRecordId(medicalRecordId);
        return clinicalNoteEntities.stream().map(clinicalNoteConverter::toClinalNoteResponse).toList();
    }
}
