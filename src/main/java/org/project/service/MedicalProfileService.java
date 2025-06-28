package org.project.service;

import org.project.model.response.MedicalProfileResponse;

import java.util.List;

public interface MedicalProfileService {
    MedicalProfileResponse getMedicalProfileOfPatient(Long patientId);
    MedicalProfileResponse updateAllergiesAndChronicDiseases(Long patientId, List<String> allergies, List<String> chronicDiseases);
}
