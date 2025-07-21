package org.project.converter;

import org.modelmapper.ModelMapper;
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

    private ModelMapperConfig modelMapperConfig;

    @Autowired
    public void setModelMapperConfig(ModelMapperConfig modelMapperConfig) {
        this.modelMapperConfig = modelMapperConfig;
    }

    public Optional<PatientEntity> toConvertEntity(PatientDTO patientDTO) {
        if (patientDTO == null) {
            return Optional.empty();
        }

        PatientEntity patientEntity = new PatientEntity();

        ModelMapper modelMapper = modelMapperConfig.mapper();

        modelMapper.map(patientDTO, patientEntity);

        patientEntity.setId(null);

        return Optional.of(patientEntity);
    }

    public PatientResponse toConvertResponse(PatientEntity patientEntity) {
        PatientResponse patientResponse = modelMapperConfig.mapper().map(patientEntity, PatientResponse.class);
        if (patientEntity.getBirthdate() != null) {
            patientResponse.setBirthdate(patientEntity.getBirthdate().toString());
        } else {
            patientResponse.setBirthdate("N/A");
        }
        return patientResponse;
    }

    public PatientResponse toResponse(PatientEntity patientEntity) {
        Optional<PatientResponse> patientResponseOptional = Optional.ofNullable(modelMapperConfig.mapper().map(patientEntity, PatientResponse.class));
        return patientResponseOptional.orElseThrow(() -> new ErrorMappingException(PatientEntity.class, PatientResponse.class));
    }
}

