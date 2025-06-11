package org.project.converter;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.project.entity.AppointmentsEntity;
import org.project.model.response.AppointmentsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Component
public class toAppointmentsResponse {
    @Autowired
    private ModelMapper modelMapper;

    public AppointmentsResponse toAppointmentResponse(AppointmentsEntity appointmentsEntity) {
        AppointmentsResponse dto = modelMapper.map(appointmentsEntity, AppointmentsResponse.class);
        dto.setPatientName(appointmentsEntity.getPatient().getFullName());
        dto.setCoordinatorName(appointmentsEntity.getCoordinatorStaff().getEmergencyContactName());
        return dto;
    }
}
