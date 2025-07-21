package org.project.service;

import org.project.model.response.AppointmentDetailsResponse;
import org.project.model.response.AppointmentsResponse;

import java.util.List;

public interface AppointmentService {
    List<AppointmentsResponse> getAllAppointments(Long id);
    AppointmentDetailsResponse getAppointment(Long id);
}
