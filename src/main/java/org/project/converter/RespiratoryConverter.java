package org.project.converter;

import org.modelmapper.ModelMapper;
import org.project.entity.RespiratoryExamEntity;
import org.project.model.response.RespiratoryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RespiratoryConverter {
    @Autowired
    private ModelMapper modelMapper;

    public RespiratoryResponse toRespiratoryResponse(RespiratoryExamEntity respiratoryExamEntity) {
        return modelMapper.map(respiratoryExamEntity, RespiratoryResponse.class);
    }
}
