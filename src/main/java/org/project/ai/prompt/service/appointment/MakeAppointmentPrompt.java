package org.project.ai.prompt.service.appointment;

import org.project.ai.converter.appointment.make_appointment.FilterDataMakeAppointment;
import org.project.ai.intent.service.appointment.make.handler.AppointmentValidationChain;
import org.project.ai.prompt.PromptAnswer;
import org.project.model.dai.AppointmentDAI;
import org.project.model.request.ChatMessageRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MakeAppointmentPrompt implements PromptAnswer {


    private final FilterDataMakeAppointment filterDataMakeAppointment;
    private final AppointmentValidationChain validationChain;

    public MakeAppointmentPrompt(FilterDataMakeAppointment filterDataMakeAppointment
    ,AppointmentValidationChain validationChain) {
        this.filterDataMakeAppointment = filterDataMakeAppointment;
        this.validationChain = validationChain;
    }

    @Override
    public String buildPrompt(ChatMessageRequest req, String history) throws IOException {
        AppointmentDAI data = filterDataMakeAppointment.extractData(req, history);
        String validationResult = validationChain.validate(data);
        if (validationResult != null) {
            return validationResult;
        }
        return "Đã đăng ký thành công";
    }
}
