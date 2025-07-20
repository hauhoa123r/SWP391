package org.project.service.impl;

import org.project.model.request.AddClinalNoteRequest;
import org.project.model.response.ClinalNoteResponse;
import org.project.service.ClinicalNoteService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClinicalNoteServiceImpl implements ClinicalNoteService {
    @Override
    public boolean addClinicalNote(Long medicalRecordId, AddClinalNoteRequest addClinalNoteRequest) {
        return false;
    }

    @Override
    public boolean updateClinicalNote(Long clinicalNoteId, AddClinalNoteRequest addClinalNoteRequest) {
        return false;
    }

    @Override
    public boolean deleteClinicalNote(Long clinicalNoteId) {
        return false;
    }

    @Override
    public List<ClinalNoteResponse> getClinicalNote(Long medicalRecordId) {
        return List.of();
    }
}
