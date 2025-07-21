package org.project.ai.intent.service.appointment.make.handler;

import org.project.ai.intent.service.appointment.AppointmentValidationHandler;
import org.project.model.dai.AppointmentDAI;
import org.springframework.stereotype.Component;

@Component
public class AppointmentValidationChain {

    private final AppointmentValidationHandler chainStart;

    public AppointmentValidationChain(
            ValidateFormStartedHandler formStarted,
            ValidateHospitalHandler hospital,
            ValidateDepartmentHandler department,
            ValidateDateHandler date,
            ValidateTimeHandler time,
            ValidatePatientHandler patient
    ){
        formStarted.setNextHandler(hospital);
        hospital.setNextHandler(department);
        department.setNextHandler(date);
        date.setNextHandler(time);
        time.setNextHandler(patient);
        chainStart = formStarted;
    }

    public String validate(AppointmentDAI data){
        return chainStart.handle(data);
    }
}
