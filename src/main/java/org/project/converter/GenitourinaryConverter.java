package org.project.converter;

import org.modelmapper.ModelMapper;
import org.project.entity.GastrointestinalExam;
import org.project.entity.GenitourinaryExam;
import org.project.model.response.GenitourinaryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GenitourinaryConverter {
    @Autowired
    private ModelMapper modelMapper;

    public GenitourinaryResponse toGenitourinaryResponse(GenitourinaryExam genitourinaryExam) {
        return modelMapper.map(genitourinaryExam, GenitourinaryResponse.class);
    }
}
