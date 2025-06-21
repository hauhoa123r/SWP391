package org.project.converter;

import org.project.config.ModelMapperConfig;
import org.project.entity.PatientEntity;
import org.project.exception.mapping.ErrorMappingException;
import org.project.model.dto.PatientDTO;
import org.project.model.response.PatientResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PatientConverter {

    private final ModelMapperConfig modelMapperConfig;

    @Autowired
    public PatientConverter(ModelMapperConfig modelMapper) {
        modelMapperConfig = modelMapper;
    }

    public Optional<PatientEntity> toEntity(PatientDTO patientDTO) {
        if (patientDTO == null) {
            return Optional.empty();
        }
        return Optional.of(modelMapperConfig.mapper().map(patientDTO, PatientEntity.class));
    }

    public PatientResponse toResponse(PatientEntity patientEntity) {
        Optional<PatientResponse> patientResponseOptional = Optional.ofNullable(modelMapperConfig.mapper().map(patientEntity, PatientResponse.class));
        return patientResponseOptional.orElseThrow(() -> new ErrorMappingException(PatientEntity.class, PatientResponse.class));
    }
}

