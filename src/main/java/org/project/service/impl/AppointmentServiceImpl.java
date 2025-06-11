package org.project.service.impl;

import org.project.converter.AppointmentConverter;
import org.project.entity.AppointmentEntity;
import org.project.enums.AppointmentStatus;
import org.project.model.dto.AppointmentDTO;
import org.project.model.response.AppointmentDetailResponse;
import org.project.model.response.AppointmentListResponse;
import org.project.repository.AppointmentRepository;
import org.project.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private AppointmentConverter appointmentConverter;


    @Override
    public List<AppointmentListResponse> getAllAppointmentIsPendingOrConfirmed(Long doctorId) {
        List<AppointmentStatus> statuses = Arrays.asList(AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED);
        List<AppointmentEntity> appointmentEntities = appointmentRepository.findByDoctorEntityIdAndAppointmentStatusIn(doctorId, statuses);
        return appointmentEntities.stream().map(appointmentConverter::toAppointmentListResponse).toList();
    }

    @Override
    public AppointmentDTO updateAppointmentStatus(AppointmentDTO appointmentDTO) {
        Optional<AppointmentEntity> appointmentEntity = appointmentRepository.findById(appointmentDTO.getId());
        if (!appointmentEntity.isPresent()) {
            throw new RuntimeException("Không tìm thấy !");
        }
        if (appointmentDTO.getAppointmentStatus() == null) {
            throw new IllegalArgumentException("Appointment status không được để null!");
        }
        AppointmentEntity appointment = appointmentEntity.get();
        appointment.setAppointmentStatus(appointmentDTO.getAppointmentStatus());
        appointmentRepository.save(appointment);
        return appointmentDTO;
    }

    @Override
    public AppointmentDetailResponse getAppointmentDetail(Long id) {
        AppointmentEntity appointmentEntity = appointmentRepository.findById(id).get();
        return appointmentConverter.toAppointmentDetailResponse(appointmentEntity);
    }
}
