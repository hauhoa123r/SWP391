package org.project.ai.intent.service.appointment.make.handler;

import org.project.ai.chat.AIService;
import org.project.model.dai.AppointmentDAI;
import org.springframework.stereotype.Component;

@Component
public class ValidateTimeHandler extends BaseValidationHandler{


    @Override
    protected boolean isValid(AppointmentDAI data) {
        if(data.getDate() == null || data.getDate().isBlank()) return false;
        return true;
    }

    @Override
    protected String errorMessage(AppointmentDAI data) {
        if(data.getDate() == null || data.getDate().isBlank()){
            return "Bạn chưa chọn ngày. Bạn muốn chọn ngày nào?";
        }
        return "Đã hết slot đặt";
    }
}
