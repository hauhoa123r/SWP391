package org.project.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.project.converter.TestItemConverter;
import org.project.model.dto.*;
import org.project.model.response.AppointmentFilterResponse;
import org.project.model.response.TestRequestResponse;
import org.project.service.AppointmentExaminationService;
import org.project.service.ExaminationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor")
public class DoctorExaminationAPI {

    @Autowired
    private AppointmentExaminationService appointmentExaminationService;

    @Autowired
    private ExaminationService examinationService;

    @Autowired
    private TestItemConverter testItemConverter;

    @GetMapping("/appointment/filter")
    public Page<AppointmentFilterResponse> filterAppointment(@ModelAttribute AppointmentFilterDTO appointmentFilterDTO){
        appointmentFilterDTO.setDoctorId(60L);
        Page<AppointmentFilterResponse> appointmentEntities = appointmentExaminationService.getAppointmentExamination(appointmentFilterDTO);
        return appointmentEntities;
    }

    @PostMapping("/add/allergies/{patientId}")
    public Boolean addAllergies(@PathVariable("patientId") Long patientId, @RequestBody List<String> allergies) throws JsonProcessingException {
        Boolean isAddAllergiesForPatient = appointmentExaminationService.isAddAllergiesForPatient(patientId, allergies);
        return true;
    }

    @PostMapping("/add/chronic/{patientId}")
    public Boolean addChronicDiseases(@PathVariable("patientId") Long patientId, @RequestBody List<String> chronic) throws JsonProcessingException {
        Boolean isAddAllergiesForPatient = appointmentExaminationService.isAddChronicDiseases(patientId, chronic);
        return true;
    }

    @PostMapping("/add/symptom/{appointmentId}")
    public Boolean addSymptom(@PathVariable("appointmentId") Long appointmentId, @RequestBody String chronic) throws JsonProcessingException {
        Boolean isAddAllergiesForPatient = appointmentExaminationService.isAddSymptoms(appointmentId, chronic);
        return true;
    }


    @PostMapping("/add/vital")
    public Boolean addVitalSign(@RequestBody VitalSignDTO vitalSignDTO){
        Boolean isAddVitalSign = examinationService.addVitalSign(vitalSignDTO);
        return true;
    }

    @PostMapping("/add/respiratory")
    public Boolean addRespiratory(@RequestBody RespiratoryDTO respiratoryDTO){
        Boolean isAddRespiratory = examinationService.addRespiratory(respiratoryDTO);
        return true;
    }

    @PostMapping("/add/cardiac")
    public Boolean addCardiac(@RequestBody CardiacDTO cardiacDTO){
        Boolean isAddCardiac = examinationService.addCardiacExam(cardiacDTO);
        return true;
    }

    @PostMapping("/add/neurologic")
    public Boolean addNeurologic(@RequestBody NeurologicDTO neurologicDTO){
        Boolean isAddNeurologic = examinationService.addNeurologic(neurologicDTO);
        return true;
    }
    @PostMapping("/add/gastro")
    public Boolean addGastro(@RequestBody GastrointestinalDTO gastrointestinalDTO){
        Boolean isAddCGastro = examinationService.addGastrointestinal(gastrointestinalDTO);
        return true;
    }

    @PostMapping("/add/musculoskeletal")
    public Boolean addMuscle(@RequestBody GastrointestinalDTO gastrointestinalDTO){
        Boolean isAddMuscle = examinationService.addGastrointestinal(gastrointestinalDTO);
        return true;
    }

    @PostMapping("/add/dermatologic")
    public Boolean addDermatologic(@RequestBody DermatologicDTO dermatologicDTO){
        Boolean isAddDermatologic = examinationService.addDermatologic(dermatologicDTO);
        return true;
    }

    @GetMapping("/filter/test/request")
    public Page<TestRequestResponse> filterTestRequest(@ModelAttribute SearchTestTypeDTO searchTestTypeDTO){
        Page<TestRequestResponse> testRequestResponses = testItemConverter.toFilterTestRequestResponse(searchTestTypeDTO);
        return testRequestResponses;
    }


    @PostMapping("/add/test/request")
    public Boolean isAddTestRequest(@RequestBody CreateTestRequestDTO createTestRequestDTO){
        Boolean isCreateTestRequest = appointmentExaminationService.createTestRequest(createTestRequestDTO);
        return true;
    }

    @GetMapping("/appointment/complete/filter")
    public Page<AppointmentFilterResponse> filterAppointmentComplete(@ModelAttribute AppointmentFilterDTO appointmentFilterDTO){
        appointmentFilterDTO.setDoctorId(60L);
        Page<AppointmentFilterResponse> appointmentEntities = appointmentExaminationService.getAppointmentCompleted(appointmentFilterDTO);
        return appointmentEntities;
    }
}
