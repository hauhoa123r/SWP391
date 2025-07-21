package org.project.service;


import org.project.model.dto.AppointmentChangeStatusDTO;
import org.project.model.response.AppointmentDetailResponse;
import org.project.model.response.AppointmentListResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AppointmentsService {
    List<AppointmentListResponse> getAllAppointmentIsPendingOrConfirmed(Long doctorId);
    AppointmentChangeStatusDTO updateAppointmentStatus(AppointmentChangeStatusDTO appointmentDTO);
    AppointmentDetailResponse getAppointmentDetail(Long id);
    List<AppointmentListResponse> getAllAppointmentInToday(Long id);
    Page<AppointmentListResponse> searchAppointments(Long doctorId, int page, int size, String search, String status, String dateFilter, String specificDate);
}
