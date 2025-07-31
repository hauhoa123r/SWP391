package org.project.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.project.converter.AppointmentFilterResponseConverter;
import org.project.converter.DoctorMedicineConverter;
import org.project.entity.*;
import org.project.enums.AppointmentStatus;
import org.project.enums.RequestStatus;
import org.project.model.dto.AppointmentFilterDTO;
import org.project.model.dto.CreateTestRequestDTO;
import org.project.model.dto.DoctorMedicineDTO;
import org.project.model.dto.TreatmentPatientDTO;
import org.project.model.response.AppointmentFilterResponse;
import org.project.model.response.DoctorMedicineResponse;
import org.project.repository.*;
import org.project.service.AppointmentExaminationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AppointmentExaminationServiceImpl implements AppointmentExaminationService {

    @Autowired
    private AppointmentFilterResponseConverter appointmentFilterResponseConverter;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MedicalProfileRepository medicalProfileRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Autowired
    private MedicalRecordSymptomRepository medicalRecordSymptomRepository;

    @Autowired
    private TestRequestRepository testRequestRepository;

    @Autowired
    private TestTypeRepository testTypeRepository;

    @Autowired
    private DoctorMedicineConverter doctorMedicineConverter;

    @Override
    public Page<AppointmentFilterResponse> getAppointmentExamination(AppointmentFilterDTO appointmentFilterDTO) {
        Page<AppointmentFilterResponse> appointmentFilterResponses = appointmentFilterResponseConverter.toConverterAppointmentFilterResponse(appointmentFilterDTO);
        return appointmentFilterResponses;
    }

    @Override
    public Boolean isAddAllergiesForPatient(Long patientId, List<String> allergies) throws JsonProcessingException {
        PatientEntity patientEntity = patientRepository.findById(patientId).orElseThrow(() -> new RuntimeException("Patient not found with id = " + patientId));

        MedicalProfileEntity medicalProfileEntity = new MedicalProfileEntity();

        if(patientEntity.getMedicalProfileEntity() != null) {
            medicalProfileEntity = patientEntity.getMedicalProfileEntity();
        }
        medicalProfileEntity.setPatientEntity(patientEntity);
        String jsonAllergies = objectMapper.writeValueAsString(allergies);
        medicalProfileEntity.setAllergies(jsonAllergies);
        medicalProfileRepository.save(medicalProfileEntity);
        return true;
    }

    @Override
    public Boolean isAddChronicDiseases(Long patientId, List<String> chronicDiseases) throws JsonProcessingException {
        PatientEntity patientEntity = patientRepository.findById(patientId).orElseThrow(() -> new RuntimeException("Patient not found with id = " + patientId));

        MedicalProfileEntity medicalProfileEntity = new MedicalProfileEntity();

        if(patientEntity.getMedicalProfileEntity() != null) {
            medicalProfileEntity = patientEntity.getMedicalProfileEntity();
        }
        medicalProfileEntity.setPatientEntity(patientEntity);
        String jsonAllergies = objectMapper.writeValueAsString(chronicDiseases);
        medicalProfileEntity.setChronicDiseases(jsonAllergies);
        medicalProfileRepository.save(medicalProfileEntity);
        return true;
    }

    @Override
    public Boolean isAddSymptoms(Long appointmentId, String chronic) throws JsonProcessingException {
        AppointmentEntity appointmentEntity = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id = " + appointmentId));

        MedicalRecordEntity medicalRecordEntity = (MedicalRecordEntity) medicalRecordRepository.findByAppointmentEntity(appointmentEntity)
                .orElse(null);

        if (medicalRecordEntity == null) {
            medicalRecordEntity = new MedicalRecordEntity();
            medicalRecordEntity.setAppointmentEntity(appointmentEntity);
            medicalRecordEntity.setPatientEntity(appointmentEntity.getPatientEntity());
            medicalRecordEntity = medicalRecordRepository.save(medicalRecordEntity);
        }

        MedicalRecordSymptomEntity symptomEntity = (MedicalRecordSymptomEntity) medicalRecordSymptomRepository.findByMedicalRecord(medicalRecordEntity)
                .orElse(null);

        if (symptomEntity == null) {
            symptomEntity = new MedicalRecordSymptomEntity();
            symptomEntity.setMedicalRecord(medicalRecordEntity);
        }

        symptomEntity.setSymptomName(chronic);
        medicalRecordSymptomRepository.save(symptomEntity);

        return true;
    }

    @Override
    public Boolean createTestRequest(CreateTestRequestDTO createTestRequestDTO) {
        ZonedDateTime vietnamTime = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        Optional<AppointmentEntity> appointmentEntity = appointmentRepository.findById(createTestRequestDTO.getAppointmentId());
        appointmentEntity.get().setAppointmentStatus(AppointmentStatus.IN_PROGRESS);
        for(Long id: createTestRequestDTO.getTestTypeIds()){
            TestRequestEntity testRequestEntity = new TestRequestEntity();
            testRequestEntity.setRequestTime(Date.from(vietnamTime.toInstant()));
            testRequestEntity.setAppointmentEntity(appointmentEntity.get());
            testRequestEntity.setPatientEntity(appointmentEntity.get().getPatientEntity());
            testRequestEntity.setRequestStatus(RequestStatus.pending);
            testRequestEntity.setDoctorEntity(appointmentEntity.get().getDoctorEntity());
            TestTypeEntity testTypeEntity = testTypeRepository.findById(id).get();
            testRequestEntity.setTestTypeEntity(testTypeEntity);
            testRequestRepository.save(testRequestEntity);
        }
        appointmentRepository.save(appointmentEntity.get());
        return true;
    }

    @Override
    public Page<AppointmentFilterResponse> getAppointmentCompleted(AppointmentFilterDTO appointmentFilterDTO) {
        Page<AppointmentFilterResponse> appointmentFilterResponses = appointmentFilterResponseConverter.toConverterAppointmentComplete(appointmentFilterDTO);
        return appointmentFilterResponses;
    }

    @Override
    public Page<DoctorMedicineResponse> filterMedicines(DoctorMedicineDTO doctorMedicineDTO) {
        Page<DoctorMedicineResponse> results = doctorMedicineConverter.toFilterMedicine(doctorMedicineDTO);
        return results;
    }

    @Override
    public Boolean isAddMedicalProfile(TreatmentPatientDTO treatmentPatientDTO) {
        AppointmentEntity appointmentEntity = appointmentRepository.findById(treatmentPatientDTO.getAppointmentId()).get();
        MedicalRecordEntity medicalRecordEntity = medicalRecordRepository.findByAppointmentEntity_Id(appointmentEntity.getId());
        medicalRecordEntity.setTreatmentPlan(treatmentPatientDTO.getTreatmentMethod());
        medicalRecordEntity.setAdmissionDate(java.sql.Date.valueOf(treatmentPatientDTO.getStartDate()));
        medicalRecordEntity.setDiagnosis(treatmentPatientDTO.getDiagnosis());
        medicalRecordEntity.setDischargeDate(java.sql.Date.valueOf(treatmentPatientDTO.getStartDate()));
        appointmentEntity.setAppointmentStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointmentEntity);
        medicalRecordRepository.save(medicalRecordEntity);
        return true;
    }

}
