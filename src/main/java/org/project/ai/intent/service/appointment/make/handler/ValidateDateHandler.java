package org.project.ai.intent.service.appointment.make.handler;

import org.project.model.dai.AppointmentDAI;
import org.springframework.stereotype.Component;

@Component
public class ValidateDateHandler extends BaseValidationHandler {

    @Override
    protected boolean isValid(AppointmentDAI data) {
        if (data.getDate() == null && data.getDate().isBlank()) return false;
        return true;
    }

    @Override
    protected String errorMessage(AppointmentDAI data) {
        return "Bạn chưa chọn ngày khám. Bạn muốn khám ngày nào?";
    }
}