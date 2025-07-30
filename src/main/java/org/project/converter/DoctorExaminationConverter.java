package org.project.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.project.entity.*;
import org.project.model.response.DoctorExaminationResponse;
import org.project.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@Transactional
public class DoctorExaminationConverter {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private MedicalProfileRepository medicalProfileRepository;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    public DoctorExaminationResponse getInformationPatientToAppointment(Long id) {
        AppointmentEntity appointmentEntity = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id = " + id));

        PatientEntity patientEntity = patientRepository.findById(appointmentEntity.getPatientEntity().getId())
                .orElseThrow(() -> new RuntimeException("Patient not found with id = " + appointmentEntity.getPatientEntity().getId()));

        DoctorExaminationResponse response = new DoctorExaminationResponse();

        Optional<MedicalProfileEntity> medicalProfileOpt = medicalProfileRepository.findByPatientEntity_Id(patientEntity.getId());

        MedicalRecordSymptomEntity medicalRecordSymptomEntity = new MedicalRecordSymptomEntity();
        response.setPatientId(patientEntity.getId());
        response.setPatientName(patientEntity.getFullName());
        response.setAge(String.valueOf(patientEntity.getBirthdate()));
        response.setEmail(patientEntity.getEmail());
        response.setPhone(patientEntity.getPhoneNumber());
        response.setAddress(patientEntity.getAddress());
        response.setGender(String.valueOf(patientEntity.getGender()));
        response.setAppointmentId(appointmentEntity.getId());
        if (medicalProfileOpt.isPresent()) {
            MedicalProfileEntity medicalProfile = medicalProfileOpt.get();

            if(medicalProfile.getAllergies() != null){
                response.setAllergies(parseJsonList(medicalProfile.getAllergies()));
            }
            else{
                response.setAllergies(Collections.singletonList(""));
            }
            if(medicalProfile.getChronicDiseases() != null) {
                response.setChronicDiseases(parseJsonList(medicalProfile.getChronicDiseases()));
            }
            else{
                response.setChronicDiseases(Collections.singletonList(""));
            }
                response.setSymptoms("Không có");
        }
        if(appointmentEntity.getMedicalRecordEntities() == null) {
            MedicalRecordEntity medicalRecordEntity = new MedicalRecordEntity();
            medicalRecordEntity.setAppointmentEntity(appointmentEntity);
            medicalRecordEntity.setPatientEntity(patientEntity);
            medicalRecordRepository.save(medicalRecordEntity);
        }
        return response;
    }

    private List<String> parseJsonList(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonString, new TypeReference<List<String>>() {});
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }
}
