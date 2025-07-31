package org.project.converter;

import org.modelmapper.ModelMapper;
import org.project.entity.ServiceFeatureEntity;
import org.project.exception.mapping.ErrorMappingException;
import org.project.model.dto.ServiceFeatureDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ServiceFeatureConverter {
    private ModelMapper modelMapper;

    @Autowired
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public ServiceFeatureEntity toEntity(ServiceFeatureDTO serviceFeatureDTO) {
        return Optional.ofNullable(modelMapper.map(serviceFeatureDTO, ServiceFeatureEntity.class))
                .orElseThrow(() -> new ErrorMappingException(ServiceFeatureEntity.class, ServiceFeatureDTO.class));
    }
}
