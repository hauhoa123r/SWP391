package org.project.converter;

import org.project.config.ModelMapperConfig;
import org.project.entity.DoctorEntity;
import org.project.exception.mapping.ErrorMappingException;
import org.project.model.response.DoctorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DoctorConverter {
    private final ModelMapperConfig modelMapperConfig;

    @Autowired
    public DoctorConverter(ModelMapperConfig modelMapperConfig) {
        this.modelMapperConfig = modelMapperConfig;
    }

    public DoctorResponse toResponse(DoctorEntity doctorEntity) {
        Optional<DoctorResponse> doctorResponseOptional = Optional.ofNullable(modelMapperConfig.mapper().map(doctorEntity, DoctorResponse.class));
        return doctorResponseOptional.orElseThrow(() -> new ErrorMappingException(DoctorEntity.class, DoctorResponse.class));
    }
}
