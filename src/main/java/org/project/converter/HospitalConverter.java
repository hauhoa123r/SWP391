package org.project.converter;

import org.project.config.ModelMapperConfig;
import org.project.entity.HospitalEntity;
import org.project.exception.mapping.ErrorMappingException;
import org.project.model.response.HospitalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class HospitalConverter {
    private ModelMapperConfig modelMapperConfig;

    @Autowired
    public void setModelMapperConfig(ModelMapperConfig modelMapperConfig) {
        this.modelMapperConfig = modelMapperConfig;
    }

    public HospitalResponse toResponse(HospitalEntity hospitalEntity) {
        Optional<HospitalResponse> hospitalResponseOptional = Optional.of(modelMapperConfig.mapper().map(hospitalEntity, HospitalResponse.class));
        return hospitalResponseOptional.orElseThrow(() -> new ErrorMappingException(HospitalEntity.class, HospitalResponse.class));
    }
}
