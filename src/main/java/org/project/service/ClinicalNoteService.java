package org.project.service;

import org.project.model.request.AddClinalNoteRequest;
import org.project.model.request.AddRespiratoryRequest;
import org.project.model.response.ClinalNoteResponse;

import java.util.List;

public interface ClinicalNoteService {
    boolean addClinicalNote(Long medicalRecordId, AddClinalNoteRequest addClinalNoteRequest);
    boolean updateClinicalNote(Long clinicalNoteId, AddClinalNoteRequest addClinalNoteRequest);
    boolean deleteClinicalNote(Long clinicalNoteId);
    List<ClinalNoteResponse> getClinicalNote(Long medicalRecordId);

}
