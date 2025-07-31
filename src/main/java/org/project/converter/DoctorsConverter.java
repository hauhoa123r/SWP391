package org.project.converter;

import org.modelmapper.ModelMapper;
import org.project.entity.DoctorEntity;
import org.project.model.response.DoctorHeaderResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DoctorsConverter {
    @Autowired
    ModelMapper modelMapper;

    public DoctorHeaderResponse toDoctorResponse(DoctorEntity doctorEntity){
        return modelMapper.map(doctorEntity, DoctorHeaderResponse.class);
    }
}