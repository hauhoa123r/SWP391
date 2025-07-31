package org.project.ai.prompt.service.appointment;

import org.project.ai.converter.appointment.make_appointment.FilterDataMakeAppointment;
import org.project.ai.converter.appointment.make_appointment.FilterDataPatientAppointment;
import org.project.ai.intent.service.appointment.make.handler.AppointmentValidationChain;
import org.project.ai.prompt.PromptAnswer;
import org.project.model.dai.AppointmentDAI;
import org.project.model.dai.PatientBasicInfo;
import org.project.model.request.ChatMessageRequest;
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

    @Override
    public String buildPrompt(ChatMessageRequest req, String history) throws IOException {
        PatientBasicInfo patientBasicInfo = filterDataPatientAppointment.extractData(req, history);

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
    private String checkRelationshipUser(PatientBasicInfo patientBasicInfo){
            return "";
    }


}
