package org.project.converter;

import org.project.config.ModelMapperConfig;
import org.project.entity.AppointmentEntity;
import org.project.exception.mapping.ErrorMappingException;
import org.project.model.dto.AppointmentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AppointmentConverter {

    private ModelMapperConfig modelMapperConfig;

    @Autowired
    public void setModelMapperConfig(ModelMapperConfig modelMapperConfig) {
        this.modelMapperConfig = modelMapperConfig;
    }

    public AppointmentEntity toEntity(AppointmentDTO appointmentDTO) {
        Optional<AppointmentEntity> appointmentEntity = Optional.ofNullable(modelMapperConfig.mapper().map(appointmentDTO, AppointmentEntity.class));
        return appointmentEntity.orElseThrow(() -> new ErrorMappingException(AppointmentDTO.class, AppointmentEntity.class));
    }
}
