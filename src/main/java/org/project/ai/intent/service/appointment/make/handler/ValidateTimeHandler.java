package org.project.ai.intent.service.appointment.make.handler;

import org.project.ai.chat.AIService;
import org.project.entity.AppointmentEntity;
import org.project.model.dai.AppointmentDAI;
import org.project.repository.AppointmentRepository;
import org.springframework.stereotype.Component;

@Component
public class ValidateTimeHandler extends BaseValidationHandler{

    private final AppointmentRepository appointmentRepository;

    public ValidateTimeHandler(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    protected boolean isValid(AppointmentDAI data) {
        if(data.getDate() == null || data.getDate().isBlank()) return false;
        AppointmentEntity appointmententity = appointmentRepository.searchAppointmentExist(data);
        if(appointmententity != null) return false;
        return true;
    }

    @Override
    protected String errorMessage(AppointmentDAI data) {
        if(data.getDate() == null || data.getDate().isBlank()){
            return "Bạn chưa chọn giờ khám bạn muốn chọn giờ nào trong ngày?";
        }
        return "Đã hết slot đặt";
    }
}
