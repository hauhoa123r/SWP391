package org.project.converter;

import org.project.entity.AppointmentEntity;
import org.project.entity.UserEntity;
import org.project.exception.ResourceNotFoundException;
import org.project.model.dto.AppointmentFilterDTO;
import org.project.model.response.AppointmentFilterResponse;
import org.project.repository.AppointmentRepository;
import org.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AppointmentFilterResponseConverter {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;
    public Page<AppointmentFilterResponse> toConverterAppointmentFilterResponse(AppointmentFilterDTO dto) {
        UserEntity userEntity = userRepository.findById(dto.getDoctorId()).orElseThrow(() -> new ResourceNotFoundException("Not found doctor"));
        dto.setDoctorId(userEntity.getStaffEntity().getId());
        dto.setStatus("CONFIRMED");
        Page<AppointmentEntity> appointmentEntities = appointmentRepository.toFilterAppointmentByDoctorIdAndPatientName(dto);

        List<AppointmentFilterResponse> responses = appointmentEntities.getContent().stream()
                .map(entity -> {
                    AppointmentFilterResponse response = new AppointmentFilterResponse();
                    response.setPatientName(entity.getPatientEntity().getFullName());
                    response.setTimeAppointment(entity.getStartTime().toString());
                    response.setAppointmentId(entity.getId());
                    response.setDepartmentName(entity.getDoctorEntity().getStaffEntity().getDepartmentEntity().getName());
                    return response;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(responses, appointmentEntities.getPageable(), appointmentEntities.getTotalElements());
    }

    public Page<AppointmentFilterResponse> toConverterAppointmentComplete(AppointmentFilterDTO dto) {
        UserEntity userEntity = userRepository.findById(dto.getDoctorId()).orElseThrow(() -> new ResourceNotFoundException("Not found doctor"));
        dto.setDoctorId(userEntity.getStaffEntity().getId());
        dto.setStatus("IN_PROGRESS");
        Page<AppointmentEntity> appointmentEntities = appointmentRepository.toFilterAppointmentByDoctorIdAndPatientName(dto);

        List<AppointmentFilterResponse> responses = appointmentEntities.getContent().stream()
                .map(entity -> {
                    AppointmentFilterResponse response = new AppointmentFilterResponse();
                    response.setPatientName(entity.getPatientEntity().getFullName());
                    response.setTimeAppointment(entity.getStartTime().toString());
                    response.setAppointmentId(entity.getId());
                    response.setDepartmentName(entity.getDoctorEntity().getStaffEntity().getDepartmentEntity().getName());
                    response.setResultURL(entity.getResultUrl());
                    response.setPhone(entity.getPatientEntity().getPhoneNumber());
                    response.setGender(String.valueOf(entity.getPatientEntity().getGender()));
                    response.setDate(String.valueOf(LocalDate.now()));
                    return response;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(responses, appointmentEntities.getPageable(), appointmentEntities.getTotalElements());
    }
}
