package org.project.converter;

import org.modelmapper.ModelMapper;
import org.project.entity.CardiacExamEntity;
import org.project.model.response.CardiacResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CardiacConverter {
    @Autowired
    private ModelMapper modelMapper;

    public CardiacResponse toCardiacResponse(CardiacExamEntity cardiacExamEntity) {
        return modelMapper.map(cardiacExamEntity, CardiacResponse.class);
    }
}
