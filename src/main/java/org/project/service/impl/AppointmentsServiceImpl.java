package org.project.service.impl;

import org.project.converter.AppointmentsConverter;
import org.project.entity.AppointmentEntity;
import org.project.enums.AppointmentStatus;
import org.project.model.dto.AppointmentChangeStatusDTO;
import org.project.model.response.AppointmentDetailResponse;
import org.project.model.response.AppointmentListResponse;
import org.project.repository.AppointmentsRepository;
import org.project.service.AppointmentsService;
import org.project.spec.AppointmentSpecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentsServiceImpl implements AppointmentsService {
    @Autowired
    private AppointmentsRepository appointmentRepository;
    @Autowired
    private AppointmentsConverter appointmentConverter;


    @Override
    public List<AppointmentListResponse> getAllAppointmentIsPendingOrConfirmed(Long doctorId) {
        List<AppointmentStatus> statuses = Arrays.asList(AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED);
        List<AppointmentEntity> appointmentEntities = appointmentRepository.findByDoctorEntityIdAndAppointmentStatusIn(doctorId, statuses);
        return appointmentEntities.stream().map(appointmentConverter::toAppointmentListResponse).toList();
    }

    @Override
    public AppointmentChangeStatusDTO updateAppointmentStatus(AppointmentChangeStatusDTO appointmentDTO) {
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

    @Override
    public List<AppointmentListResponse> getAllAppointmentInToday(Long doctorId) {
        LocalDate today = LocalDate.now();
        Timestamp start = Timestamp.valueOf(today.atStartOfDay());
        Timestamp end = Timestamp.valueOf(today.plusDays(1).atStartOfDay());

        List<AppointmentEntity> appointmentEntities = appointmentRepository.findTodayAppointmentsByDoctorId(doctorId,start,end);
        return appointmentEntities.stream().map(appointmentConverter::toAppointmentListResponse).toList();
    }

    @Override
    public Page<AppointmentListResponse> searchAppointments(Long doctorId, int page, int size, String search, String status, String dateFilter, String specificDate) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("startTime").descending());
        Specification<AppointmentEntity> spec = AppointmentSpecs.byDoctorId(doctorId)
                .and(AppointmentSpecs.search(search))
                .and(AppointmentSpecs.status(status))
                .and(AppointmentSpecs.dateFilter(dateFilter, specificDate));
        Page<AppointmentEntity> pageResult = appointmentRepository.findAll(spec, pageable);
        return pageResult.map(appointmentConverter::toAppointmentListResponse);
    }
}
