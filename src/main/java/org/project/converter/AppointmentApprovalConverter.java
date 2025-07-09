package org.project.converter;

import org.project.entity.AppointmentEntity;
import org.project.entity.DoctorEntity;
import org.project.entity.SchedulingCoordinatorEntity;
import org.project.enums.AppointmentStatus;
import org.project.model.dto.ChangeAppointmentDTO;
import org.project.model.response.AppointmentApprovalResponse;
import org.project.repository.DoctorRepository;
import org.project.repository.SchedulingCoordinatorRepository;
import org.project.repository.StaffRepository;
import org.project.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class AppointmentApprovalConverter {

    private final PatientService patientService;

    private final DoctorRepository doctorRepository;

    private final StaffRepository staffRepository;

    private final SchedulingCoordinatorRepository schedulingCoordinatorRepository;

    @Autowired
    public AppointmentApprovalConverter(PatientService patientService,
                                        DoctorRepository doctorRepository,
                                        StaffRepository staffRepository,
                                        SchedulingCoordinatorRepository schedulingCoordinatorRepository) {
        this.patientService = patientService;
        this.doctorRepository = doctorRepository;
        this.staffRepository = staffRepository;
        this.schedulingCoordinatorRepository = schedulingCoordinatorRepository;
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
            appointmentApprovalResponse.setPatientId(appointmentEntity.getPatientEntity().getId());
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

        if (!conflictIds.isEmpty()) {
            appointmentApprovalResponse.setStatus(AppointmentStatus.PENDING.getStatus());
            appointmentApprovalResponse.setHospitalId(appointmentEntity.getDoctorEntity().getStaffEntity().getHospitalEntity().getId());
            appointmentApprovalResponse.setDoctorId(appointmentEntity.getDoctorEntity().getId());
        } else {
            appointmentApprovalResponse.setStatus(AppointmentStatus.CONFLICTED.getStatus());
        }
        return appointmentApprovalResponse;
    }

    public AppointmentEntity convertToEntity(AppointmentEntity appointmentEntity, ChangeAppointmentDTO changeAppointmentDTO) {
        DoctorEntity doctorEntity = doctorRepository.findById(changeAppointmentDTO.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + changeAppointmentDTO.getDoctorId()));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(changeAppointmentDTO.getAppointmentDate(), formatter);
        Timestamp startTime = Timestamp.valueOf(dateTime);
        SchedulingCoordinatorEntity schedulingCoordinatorEntity = schedulingCoordinatorRepository.findById(changeAppointmentDTO.getStaffScheduleId())
                .orElseThrow(() -> new RuntimeException("Scheduling Coordinator not found with id: " + changeAppointmentDTO.getStaffScheduleId()));

        appointmentEntity.setDoctorEntity(doctorEntity);
        appointmentEntity.setAppointmentStatus(AppointmentStatus.CONFIRMED);
        appointmentEntity.setStartTime(startTime);
        appointmentEntity.setSchedulingCoordinatorEntity(schedulingCoordinatorEntity);

        return appointmentEntity;
    }
}
