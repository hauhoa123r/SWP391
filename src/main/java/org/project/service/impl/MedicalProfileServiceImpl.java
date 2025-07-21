package org.project.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.project.entity.MedicalProfileEntity;
import org.project.entity.PatientEntity;
import org.project.exception.ResourceNotFoundException;
import org.project.model.dto.MedicalProfileDTO;
import org.project.model.dto.PatientDTO;
import org.project.model.response.MedicalProfileResponse;
import org.project.model.response.PatientResponse;
import org.project.repository.MedicalProfileRepository;
import org.project.repository.PatientRepository;
import org.project.service.MedicalProfileService;
import org.project.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Transactional
@Service
public class MedicalProfileServiceImpl implements MedicalProfileService {

    private PatientRepository patientRepositoryImpl;

    private MedicalProfileRepository medicalProfileRepositoryImpl;

    private PatientService patientServiceImpl;

    @Autowired
    public MedicalProfileServiceImpl(PatientRepository patientRepository, MedicalProfileRepository medicalProfileRepository, PatientService patientService) {
        this.patientRepositoryImpl = patientRepository;
        this.medicalProfileRepositoryImpl = medicalProfileRepository;
        this.patientServiceImpl = patientService;
    }

    @Override
    public void createMedicalProfile(MedicalProfileDTO medicalProfileDTO) {
        MedicalProfileEntity medicalProfileEntity = new MedicalProfileEntity();

        ObjectMapper mapper = new ObjectMapper();

        try {
            String jsonAllergies = mapper.writeValueAsString(medicalProfileDTO.getAllergies());
            String jsonChronicDiseases = mapper.writeValueAsString(medicalProfileDTO.getChronicDiseases());

            medicalProfileEntity.setAllergies(jsonAllergies);
            medicalProfileEntity.setChronicDiseases(jsonChronicDiseases);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Json mapping error", e);
        }

        PatientEntity patientEntity = patientRepositoryImpl.findById(medicalProfileDTO.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + medicalProfileDTO.getPatientId()));

        medicalProfileEntity.setPatientEntity(patientEntity);

        medicalProfileRepositoryImpl.save(medicalProfileEntity);
    }

    @Override
    public MedicalProfileResponse getMedicalProfileByPatientId(Long Id) {
        Optional<MedicalProfileEntity> medicalProfileEntityOptional = medicalProfileRepositoryImpl.findByPatientEntity_Id(Id);
        if (medicalProfileEntityOptional != null) {
            MedicalProfileResponse response = new MedicalProfileResponse();
            String allergies = medicalProfileEntityOptional.get().getAllergies();
            String chronicDiseases = medicalProfileEntityOptional.get().getChronicDiseases();

            String[] allergiesArray = allergies.split(",");
            String[] chronicDiseasesArray = chronicDiseases.split(",");

            Set<String> allergiesSet = Set.of(allergiesArray);
            Set<String> chronicDiseasesSet = Set.of(chronicDiseasesArray);

            response.setAllergies(allergiesSet);
            response.setChronicDiseases(chronicDiseasesSet);
            response.setPatientId(medicalProfileEntityOptional.get().getPatientEntity().getId());
            return response;
        }
        throw new ResourceNotFoundException("Medical profile not found for patient with id: " + Id);
    }

    @Override
    public List<MedicalProfileResponse> getAllMedicalProfiles() {
    List<MedicalProfileEntity> medicalProfileEntities = medicalProfileRepositoryImpl.findAll();
        if (medicalProfileEntities != null && !medicalProfileEntities.isEmpty()) {
            return medicalProfileEntities.stream()
                    .map(entity -> {
                        MedicalProfileResponse response = new MedicalProfileResponse();
                        response.setPatientId(entity.getPatientEntity().getId());
                        response.setAllergies(Set.of(entity.getAllergies().split(",")));
                        response.setChronicDiseases(Set.of(entity.getChronicDiseases().split(",")));
                        return response;
                    })
                    .toList();
        }
        throw new ResourceNotFoundException("No medical profiles found.");
    }

    @Override
    public void updateMedicalProfile(Long medicalProfileId, MedicalProfileDTO medicalProfileDTO) {
        Optional<MedicalProfileEntity> medicalProfileEntityOptional = medicalProfileRepositoryImpl.findById(medicalProfileId);
        if (medicalProfileEntityOptional.isPresent()) {
            MedicalProfileEntity medicalProfileEntity = medicalProfileEntityOptional.get();

            medicalProfileEntity.setAllergies(medicalProfileDTO.getAllergies().toString());
            medicalProfileEntity.setChronicDiseases(medicalProfileDTO.getChronicDiseases().toString());

            PatientEntity patientEntity = patientRepositoryImpl.findById(medicalProfileDTO.getPatientId())
                    .orElseThrow(() -> new RuntimeException("Patient not found with id: " + medicalProfileDTO.getPatientId()));

            medicalProfileEntity.setPatientEntity(patientEntity);

            medicalProfileRepositoryImpl.save(medicalProfileEntity);
        } else {
            throw new ResourceNotFoundException("Medical profile not found with id: " + medicalProfileId);
        }
    }

    @Override
    public PatientResponse addPatientAndMedicalProfile(MedicalProfileDTO medicalProfileDTO, PatientDTO patientDTO) {
    Long patientId = patientServiceImpl.createPatient(patientDTO);

    medicalProfileDTO.setPatientId(patientId);

    createMedicalProfile(medicalProfileDTO);

    return patientServiceImpl.getPatientById(patientId);
    }
}
