package org.project.ai.prompt.service.appointment;

import org.aspectj.asm.internal.Relationship;
import org.project.ai.converter.appointment.make_appointment.FilterDataMakeAppointment;
import org.project.ai.converter.appointment.make_appointment.FilterDataPatientAppointment;
import org.project.ai.intent.service.appointment.make.handler.AppointmentValidationChain;
import org.project.ai.prompt.PromptAnswer;
import org.project.entity.PatientEntity;
import org.project.entity.UserEntity;
import org.project.enums.FamilyRelationship;
import org.project.model.dai.AppointmentDAI;
import org.project.model.dai.PatientBasicInfo;
import org.project.model.request.ChatMessageRequest;
import org.project.repository.PatientRepository;
import org.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MakeAppointmentPrompt implements PromptAnswer {


    private final FilterDataMakeAppointment filterDataMakeAppointment;
    private final AppointmentValidationChain validationChain;
    private final FilterDataPatientAppointment filterDataPatientAppointment;
    public MakeAppointmentPrompt(FilterDataMakeAppointment filterDataMakeAppointment,
                                 AppointmentValidationChain validationChain,
                                 FilterDataPatientAppointment filterDataPatientAppointment) {
        this.filterDataMakeAppointment = filterDataMakeAppointment;
        this.validationChain = validationChain;
        this.filterDataPatientAppointment = filterDataPatientAppointment;
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Override
    public String buildPrompt(ChatMessageRequest req, String history) throws IOException {
        PatientBasicInfo patientBasicInfo = filterDataPatientAppointment.extractData(req, history);
        UserEntity userEntity = userRepository.findById(patientBasicInfo.getUserId()).get();
        PatientEntity patientEntity = patientRepository.findByUserEntity_IdAndFamilyRelationship(userEntity.getId()
                , FamilyRelationship.SELF);

        if(patientBasicInfo.getFullName() == null){
            return validatePatient(patientBasicInfo);
        }

        AppointmentDAI data = filterDataMakeAppointment.extractData(req, history);
        String validationResult = validationChain.validate(data);
        if (validationResult != null) {
            return validationResult;
        }
        return "Đã đăng ký thành công";
    }

    private String validatePatient(PatientBasicInfo patientBasicInfo){
       return "Bạn muốn đặt lịch cho ai, họ đang từng khám ở cơ sở bệnh viện nào thuộc về hệ thống của chúng tôi chưa?" +
               "nếu chưa bạn hãy tạo hồ sơ trước nhé!";
    }
}
