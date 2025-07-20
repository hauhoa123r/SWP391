package org.project.converter;

import org.modelmapper.ModelMapper;
import org.project.entity.GastrointestinalExam;
import org.project.model.response.GastrointestinalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GastrointestinalConverter {
    @Autowired
    private ModelMapper modelMapper;

    public GastrointestinalResponse toGastrointestinalResponse(GastrointestinalExam gastrointestinalExam) {
        return modelMapper.map(gastrointestinalExam, GastrointestinalResponse.class);
    }
}
