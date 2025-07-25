package org.project.converter;

import org.project.config.ModelMapperConfig;
import org.project.entity.ServiceEntity;
import org.project.exception.mapping.ErrorMappingException;
import org.project.model.response.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ServiceConverter {

    private ModelMapperConfig modelMapperConfig;

    @Autowired
    public void setModelMapperConfig(ModelMapperConfig modelMapperConfig) {
        this.modelMapperConfig = modelMapperConfig;
    }

    public ServiceResponse toResponse(ServiceEntity serviceEntity) {
        Optional<ServiceResponse> serviceResponseOptional = Optional.ofNullable(modelMapperConfig.mapper().map(serviceEntity, ServiceResponse.class));
        return serviceResponseOptional.orElseThrow(() -> new ErrorMappingException(ServiceResponse.class, ServiceEntity.class));
    }
}
