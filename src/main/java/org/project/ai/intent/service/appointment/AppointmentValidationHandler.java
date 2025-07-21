package org.project.ai.intent.service.appointment;

import org.project.model.dai.AppointmentDAI;

public interface AppointmentValidationHandler {
    void setNextHandler(AppointmentValidationHandler nextHandler);
    String handle(AppointmentDAI data);
}
