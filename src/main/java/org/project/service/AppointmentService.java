package org.project.service;

import org.project.enums.AppointmentStatus;
import org.project.model.dto.AppointmentDTO;
import org.project.model.dto.ChangeAppointmentDTO;
import org.project.model.response.AppointmentApprovalResponse;
import org.project.model.response.AppointmentAvailableResponse;

import java.sql.Timestamp;
import java.util.List;

public interface AppointmentService {

    void saveAppointment(AppointmentDTO appointmentDTO);

    Long countCompletedAppointmentsByDepartment(Long departmentId);

    Long countCompletedAppointments();

    List<AppointmentApprovalResponse> getAppointmentsHaveStatusPendingByStaffId(Long staffId);

    Boolean changeStatus(Long appointmentId, AppointmentStatus status, Long scheduleCoordinatorId);

    AppointmentAvailableResponse getAvailableAppointmentsByDoctorIdForSuggestion(Long staffId, Long patientId, Timestamp availableTimeStamp, int maxDays);

    Boolean changeAppointment(ChangeAppointmentDTO changeAppointmentDTO);

}
