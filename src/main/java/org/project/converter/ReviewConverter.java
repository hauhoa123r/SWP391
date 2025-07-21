package org.project.converter;

import org.modelmapper.ModelMapper;
import org.project.entity.ReviewEntity;
import org.project.exception.mapping.ErrorMappingException;
import org.project.model.response.ReviewResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ReviewConverter {
    private ModelMapper modelMapper;

    @Autowired
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public ReviewResponse toResponse(ReviewEntity reviewEntity) {
        return Optional.ofNullable(modelMapper.map(reviewEntity, ReviewResponse.class))
                .orElseThrow(() -> new ErrorMappingException(ReviewEntity.class, ReviewResponse.class));
    }
}
