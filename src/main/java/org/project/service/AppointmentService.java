package org.project.service;


import org.project.model.dto.AppointmentDTO;
import org.project.model.response.AppointmentDetailResponse;
import org.project.model.response.AppointmentListResponse;

import java.util.List;

public interface AppointmentService {
    List<AppointmentListResponse> getAllAppointmentIsPendingOrConfirmed(Long doctorId);
    AppointmentDTO updateAppointmentStatus(AppointmentDTO appointmentDTO);
    AppointmentDetailResponse getAppointmentDetail(Long id);
    List<AppointmentListResponse> getAllAppointmentInToday(Long id);
}
