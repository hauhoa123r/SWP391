package org.project.service;

import org.project.model.dto.AppointmentDTO;

public interface AppointmentService {

    void saveAppointment(AppointmentDTO appointmentDTO);

    Long countCompletedAppointmentsByDepartment(Long departmentId);

    Long countCompletedAppointments();
}
