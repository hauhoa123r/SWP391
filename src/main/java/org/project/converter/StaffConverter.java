package org.project.converter;

import org.project.config.ModelMapperConfig;
import org.project.entity.ReviewEntity;
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
        this.modelMapperConfig.mapper().typeMap(StaffEntity.class, StaffResponse.class).setPostConverter(context -> {
            StaffEntity staffEntity = context.getSource();
            StaffResponse staffResponse = context.getDestination();

            staffResponse.setReviewCount(staffEntity.getReviewEntities().size());
            staffResponse.setAverageRating(staffEntity.getReviewEntities().stream().mapToDouble(ReviewEntity::getRating).average().orElse(0.0));

            return staffResponse;
        });
    }

    public StaffResponse toResponse(StaffEntity staffEntity) {
        Optional<StaffResponse> staffResponseOptional = Optional.ofNullable(modelMapperConfig.mapper().map(staffEntity, StaffResponse.class));
        return staffResponseOptional.orElseThrow(() -> new ErrorMappingException(StaffEntity.class, StaffResponse.class));
    }
}
