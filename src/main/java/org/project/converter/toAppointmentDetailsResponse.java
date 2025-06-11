package org.project.converter;

import org.modelmapper.ModelMapper;
import org.project.entity.AppointmentsEntity;
import org.project.model.response.AppointmentDetailsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class toAppointmentDetailsResponse {
    @Autowired
    private ModelMapper modelMapper;

    public AppointmentDetailsResponse toAppointmentDetailsResponse(AppointmentsEntity entity) {
        return modelMapper.map(entity, AppointmentDetailsResponse.class);
    }
}
