package org.project.repository.impl;

import org.project.entity.AppointmentEntity;
import org.project.model.dai.AppointmentDAI;


public interface AppointmentRepositoryCustom {
    public AppointmentEntity searchAppointmentExist(AppointmentDAI appointmentDai);
}
