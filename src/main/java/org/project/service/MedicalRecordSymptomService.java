package org.project.service;

import org.project.model.request.MedicalRecordSymptomRequest;
import org.project.model.response.MedicalRecordSymptomResponse;

import java.util.List;

public interface MedicalRecordSymptomService {
    List<MedicalRecordSymptomResponse> getSymptoms(Long medicalRecordId);
    List<Long> addMedicalRecordSymptom(Long medicalRecordId, List<MedicalRecordSymptomRequest> symptoms);
    void updateMedicalRecordSymptom(Long symptomId, MedicalRecordSymptomRequest request);
    void deleteMedicalRecordSymptom(Long symptomId);
}

