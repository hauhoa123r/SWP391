package org.project.service;

import org.project.model.dto.AppointmentDTO;

import java.util.Map;

public interface AppointmentService {

    Map<String, Object> saveAppointment(AppointmentDTO appointmentDTO);
}
