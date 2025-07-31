package org.project.converter;

import org.project.config.ModelMapperConfig;
import org.project.entity.AppointmentEntity;
import org.project.entity.HospitalEntity;
import org.project.exception.mapping.ErrorMappingException;
import org.project.model.dai.AppointmentDAI;
import org.project.model.dto.AppointmentDTO;
import org.project.model.dto.MakeAppointmentDTO;
import org.project.model.response.AppointmentCustomerResponse;
import org.project.model.response.AppointmentDashboardCustomerResponse;
import org.project.model.response.AppointmentResponse;
import org.project.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class AppointmentConverter {

    private ModelMapperConfig modelMapperConfig;
    private HospitalRepository hospitalRepository;

    @Autowired
    public void setModelMapperConfig(ModelMapperConfig modelMapperConfig) {
        this.modelMapperConfig = modelMapperConfig;
    }

    @Autowired
    public void setHospitalRepository(HospitalRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
    }

    public AppointmentEntity toEntity(AppointmentDTO appointmentDTO) {
        Optional<AppointmentEntity> appointmentEntity = Optional.ofNullable(modelMapperConfig.mapper().map(appointmentDTO, AppointmentEntity.class));
        return appointmentEntity.orElseThrow(() -> new ErrorMappingException(AppointmentDTO.class, AppointmentEntity.class));
    }

    public AppointmentResponse toResponse(AppointmentEntity appointmentEntity) {
        return Optional.ofNullable(modelMapperConfig.mapper().map(appointmentEntity, AppointmentResponse.class))
                .orElseThrow(() -> new ErrorMappingException(AppointmentEntity.class, AppointmentResponse.class));
    }

    public MakeAppointmentDTO toConverterMakeAppointmentDTO(AppointmentDAI appointmentDAI) {
        MakeAppointmentDTO makeAppointmentDTO = new MakeAppointmentDTO();
        makeAppointmentDTO.setDoctorName(appointmentDAI.getDoctorName());
        HospitalEntity hospitalEntity = hospitalRepository.findByNameContaining(appointmentDAI.getHospitalName());
        if (hospitalEntity == null) {
            makeAppointmentDTO.setHospitalName("Hospital does not exist in system!");
            makeAppointmentDTO.setHospitalAddress("?");
            makeAppointmentDTO.setDepartmentName("?");
        } else {
            makeAppointmentDTO.setHospitalName(hospitalEntity.getName());
            makeAppointmentDTO.setHospitalAddress(hospitalEntity.getAddress());
            makeAppointmentDTO.setDepartmentName(appointmentDAI.getDepartmentName());
        }
        makeAppointmentDTO.setStartDate(appointmentDAI.getDate() + " " + appointmentDAI.getTime());
        return makeAppointmentDTO;
    }

    public AppointmentDashboardCustomerResponse toConverterAppointmentDashboardCustomerResponse(AppointmentEntity appointmentEntity) {
        AppointmentDashboardCustomerResponse appointmentDashboardCustomerResponse = new AppointmentDashboardCustomerResponse();

        Timestamp startTime = appointmentEntity.getStartTime();

        LocalDateTime startDateTime = startTime.toLocalDateTime();

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

        String date  = startDateTime.format(dateFormat);
        String time = startDateTime.format(timeFormat);

        appointmentDashboardCustomerResponse.setId(appointmentEntity.getId());
        appointmentDashboardCustomerResponse.setPatientName(appointmentEntity.getPatientEntity().getFullName());
        appointmentDashboardCustomerResponse.setDoctorName(appointmentEntity.getDoctorEntity().getStaffEntity().getFullName());
        appointmentDashboardCustomerResponse.setStatus(appointmentEntity.getAppointmentStatus().toString());
        appointmentDashboardCustomerResponse.setDate(date);
        appointmentDashboardCustomerResponse.setTime(time);

        return appointmentDashboardCustomerResponse;
    }

    public AppointmentCustomerResponse toConverterAppointmentCustomerResponse(AppointmentEntity appointmentEntity) {
        Timestamp startTime = appointmentEntity.getStartTime();

        LocalDateTime startDateTime = startTime.toLocalDateTime();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        String dateTime = startDateTime.format(dateTimeFormatter);

        AppointmentCustomerResponse appointmentCustomerResponse = new AppointmentCustomerResponse();
        appointmentCustomerResponse.setId(appointmentEntity.getId());
        appointmentCustomerResponse.setPatientName(appointmentEntity.getPatientEntity().getFullName());
        appointmentCustomerResponse.setRelationship(appointmentEntity.getPatientEntity().getFamilyRelationship().getRelationship());
        appointmentCustomerResponse.setDoctorName(appointmentEntity.getDoctorEntity().getStaffEntity().getFullName());
        appointmentCustomerResponse.setServiceName(appointmentEntity.getServiceEntity().getProductEntity().getName());
        appointmentCustomerResponse.setStatus(appointmentEntity.getAppointmentStatus().toString());
        appointmentCustomerResponse.setResultUrl(appointmentEntity.getResultUrl());
        appointmentCustomerResponse.setStartTime(dateTime);
        return appointmentCustomerResponse;
    }

}
