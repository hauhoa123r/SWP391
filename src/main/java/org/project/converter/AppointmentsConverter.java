package org.project.converter;

import org.modelmapper.ModelMapper;
import org.project.entity.AppointmentEntity;
import org.project.model.response.AppointmentDetailResponse;
import org.project.model.response.AppointmentListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppointmentsConverter {
    @Autowired
    private ModelMapper modelMapper;

    public AppointmentListResponse toAppointmentListResponse(AppointmentEntity appointmentEntity) {
        return modelMapper.map(appointmentEntity, AppointmentListResponse.class);
    }

    public AppointmentDetailResponse toAppointmentDetailResponse(AppointmentEntity appointmentEntity) {
        return modelMapper.map(appointmentEntity, AppointmentDetailResponse.class);
    }
}
