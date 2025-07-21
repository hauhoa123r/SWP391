package org.project.service;

import org.project.model.response.MedicalProfilesResponse;

import java.util.List;

public interface MedicalProfilesService {
    MedicalProfilesResponse getMedicalProfileOfPatient(Long patientId);
    MedicalProfilesResponse updateAllergiesAndChronicDiseases(Long patientId, List<String> allergies, List<String> chronicDiseases);
}
