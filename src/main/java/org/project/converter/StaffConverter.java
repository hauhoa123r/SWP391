package org.project.converter;

import org.project.config.ModelMapperConfig;
import org.project.entity.StaffEntity;
import org.project.exception.mapping.ErrorMappingException;
import org.project.model.response.StaffResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class StaffConverter {

    private ModelMapperConfig modelMapperConfig;

    @Autowired
    public void setModelMapperConfig(ModelMapperConfig modelMapperConfig) {
        this.modelMapperConfig = modelMapperConfig;
    }

    public StaffResponse toResponse(StaffEntity staffEntity) {
        Optional<StaffResponse> staffResponseOptional = Optional.ofNullable(modelMapperConfig.mapper().map(staffEntity, StaffResponse.class));
        return staffResponseOptional.orElseThrow(() -> new ErrorMappingException(StaffEntity.class, StaffResponse.class));
    }
}
