package org.project.ai.intent.service.appointment.make.handler;

import org.project.model.dai.AppointmentDAI;
import org.project.repository.StaffRepository;
import org.springframework.stereotype.Component;

@Component
public class ValidateDoctorHandler extends BaseValidationHandler{

    private final StaffRepository staffRepository;

    public ValidateDoctorHandler(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    @Override
    protected boolean isValid(AppointmentDAI data) {
        return true;
    }

    @Override
    protected String errorMessage(AppointmentDAI data) {
        return "";
    }
}
