package org.project.service;

import org.project.model.response.MedicalProfileVResponse;

import java.util.List;

public interface MedicalProfilesService {
    MedicalProfileVResponse getMedicalProfileOfPatient(Long patientId);
    MedicalProfileVResponse updateAllergiesAndChronicDiseases(Long patientId, List<String> allergies, List<String> chronicDiseases);
}
