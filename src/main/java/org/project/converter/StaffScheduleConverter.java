package org.project.converter;

import org.project.config.ModelMapperConfig;
import org.project.entity.StaffEntity;
import org.project.entity.StaffScheduleEntity;
import org.project.exception.mapping.ErrorMappingException;
import org.project.model.response.StaffScheduleResponse;
import org.project.model.response.StaffSubstituteResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class StaffScheduleConverter {

    private ModelMapperConfig modelMapperConfig;

    @Autowired
    public void setModelMapperConfig(ModelMapperConfig modelMapperConfig) {
        this.modelMapperConfig = modelMapperConfig;
    }

    public StaffScheduleResponse toResponse(StaffScheduleEntity staffScheduleEntity) {
        Optional<StaffScheduleResponse> staffScheduleResponseOptional = Optional.ofNullable(modelMapperConfig.mapper().map(staffScheduleEntity, StaffScheduleResponse.class));
        return staffScheduleResponseOptional.orElseThrow(() -> new ErrorMappingException(StaffScheduleEntity.class, StaffScheduleResponse.class));
    }

    public StaffSubstituteResponse toResponseSubstitute(StaffEntity staffEntity) {
        StaffSubstituteResponse staffSubstituteResponse = new StaffSubstituteResponse();
        staffSubstituteResponse.setId(staffEntity.getId());
        staffSubstituteResponse.setName(staffEntity.getFullName());
        return staffSubstituteResponse;
    }
}
