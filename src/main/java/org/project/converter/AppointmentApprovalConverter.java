package org.project.converter;

import org.project.entity.AppointmentEntity;
import org.project.enums.AppointmentStatus;
import org.project.model.response.AppointmentApprovalResponse;
import org.project.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class AppointmentApprovalConverter {

    private final PatientService patientService;

    @Autowired
    public AppointmentApprovalConverter(PatientService patientService) {
        this.patientService = patientService;
    }

    public AppointmentApprovalResponse convertToResponse (AppointmentEntity appointmentEntity, List<Long> conflictIds) {
        AppointmentApprovalResponse appointmentApprovalResponse = new AppointmentApprovalResponse();

        appointmentApprovalResponse.setAppointmentId(appointmentEntity.getId());

        if (appointmentEntity.getDoctorEntity() != null
            && appointmentEntity.getDoctorEntity().getStaffEntity() != null) {
            appointmentApprovalResponse.setDoctorName(appointmentEntity.getDoctorEntity().getStaffEntity().getFullName());
        }

        if (appointmentEntity.getPatientEntity() != null) {
            appointmentApprovalResponse.setPatientName(appointmentEntity.getPatientEntity().getFullName());
            appointmentApprovalResponse.setPatientEmail(appointmentEntity.getPatientEntity().getEmail());
            appointmentApprovalResponse.setPatientPhoneNumber(appointmentEntity.getPatientEntity().getPhoneNumber());

            try {
                appointmentApprovalResponse.setPatientAvatarBase64(patientService.toConvertFileToBase64(appointmentEntity.getPatientEntity().getAvatarUrl()));
            } catch (Exception e) {
                appointmentApprovalResponse.setPatientAvatarBase64(null); // Set to null if conversion fails
            }
        }

        appointmentApprovalResponse.setAppointmentConflictIds(conflictIds);

        if (appointmentEntity.getStartTime() != null) {
            LocalDateTime dateTime = appointmentEntity.getStartTime().toLocalDateTime();
            appointmentApprovalResponse.setDate(dateTime.toLocalDate().toString());
            appointmentApprovalResponse.setStartTime(dateTime.toLocalTime().format(DateTimeFormatter.ISO_LOCAL_TIME));
        }

        if (conflictIds.isEmpty()) {
            appointmentApprovalResponse.setStatus(AppointmentStatus.PENDING.getStatus());
        } else {
            appointmentApprovalResponse.setStatus(AppointmentStatus.CONFLICTED.getStatus());
        }
        return appointmentApprovalResponse;
    }

}
