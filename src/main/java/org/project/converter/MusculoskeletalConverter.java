package org.project.converter;

import org.modelmapper.ModelMapper;
import org.project.entity.MusculoskeletalExamEntity;
import org.project.model.response.MusculoskeletalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MusculoskeletalConverter {
    @Autowired
    private ModelMapper modelMapper;

    public MusculoskeletalResponse toMusculoskeletalResponse(MusculoskeletalExamEntity musculoskeletalExamEntity) {
        return modelMapper.map(musculoskeletalExamEntity, MusculoskeletalResponse.class);
    }
}
