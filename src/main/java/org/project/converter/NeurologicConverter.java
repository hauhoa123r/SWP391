package org.project.converter;

import org.modelmapper.ModelMapper;
import org.project.entity.NeurologicExamEntity;
import org.project.model.response.NeurologicResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NeurologicConverter {
    @Autowired
    private ModelMapper modelMapper;

    public NeurologicResponse toNeurologicResponse(NeurologicExamEntity neurologicExamEntity) {
        return modelMapper.map(neurologicExamEntity, NeurologicResponse.class);
    }
}
