package org.project.converter;

import org.modelmapper.ModelMapper;
import org.project.entity.DermatologicExamEntity;
import org.project.model.response.DermatologicResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DermatologicConverter {
    @Autowired
    private ModelMapper modelMapper;

    public DermatologicResponse toDermatologicResponse(DermatologicExamEntity examEntity) {
        return modelMapper.map(examEntity, DermatologicResponse.class);
    }
}
