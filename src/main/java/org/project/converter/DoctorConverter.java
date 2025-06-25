package org.project.converter;

import org.modelmapper.ModelMapper;
import org.project.entity.DoctorEntity;
import org.project.model.response.DoctorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DoctorConverter {
    @Autowired
    ModelMapper modelMapper;

    public DoctorResponse toDoctorResponse(DoctorEntity doctorEntity){
        return modelMapper.map(doctorEntity,DoctorResponse.class);
    }
}
