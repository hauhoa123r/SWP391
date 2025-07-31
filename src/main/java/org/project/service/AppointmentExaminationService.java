package org.project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.project.model.dto.AppointmentFilterDTO;
import org.project.model.dto.CreateTestRequestDTO;
import org.project.model.dto.DoctorMedicineDTO;
import org.project.model.dto.TreatmentPatientDTO;
import org.project.model.response.AppointmentFilterResponse;
import org.project.model.response.DoctorMedicineResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AppointmentExaminationService {
    Page<AppointmentFilterResponse> getAppointmentExamination(AppointmentFilterDTO appointmentFilterDTO);
    Boolean isAddAllergiesForPatient(Long patientId, List<String>allergies) throws JsonProcessingException;
    Boolean isAddChronicDiseases(Long patientId, List<String>allergies) throws JsonProcessingException;
    Boolean isAddSymptoms(Long patientId, String chronic) throws JsonProcessingException;
    Boolean createTestRequest(CreateTestRequestDTO createTestRequestDTO);
    Page<AppointmentFilterResponse> getAppointmentCompleted(AppointmentFilterDTO appointmentFilterDTO);
    Page<DoctorMedicineResponse> filterMedicines(DoctorMedicineDTO doctorMedicineDTO);
    Boolean isAddMedicalProfile(TreatmentPatientDTO treatmentPatientDTO);
}
