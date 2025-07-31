package org.project.ai.intent.service.appointment.make.handler;

import org.project.ai.intent.service.appointment.AppointmentValidationHandler;
import org.project.model.dai.AppointmentDAI;

public abstract class BaseValidationHandler implements AppointmentValidationHandler {

    protected AppointmentValidationHandler next;

    @Override
    public void setNextHandler(AppointmentValidationHandler next) {
        this.next = next;
    }


    public String handle(AppointmentDAI data) {
        if (!isValid(data)) {
            return errorMessage(data);
        }
        return next != null ? next.handle(data) : null;
    }

    protected abstract boolean isValid(AppointmentDAI data);
    protected abstract String errorMessage(AppointmentDAI data);

}
