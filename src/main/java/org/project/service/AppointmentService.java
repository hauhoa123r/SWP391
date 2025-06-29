package org.project.service;

import org.project.model.dto.AppointmentDTO;
import org.project.model.response.AppointmentApprovalResponse;

import java.util.List;
import java.util.Map;

public interface AppointmentService {

    Map<String, Object> saveAppointment(AppointmentDTO appointmentDTO);

    List<AppointmentApprovalResponse> getAppointmentsHaveStatusPendingByHospitalId(Long hospitalId);

}
