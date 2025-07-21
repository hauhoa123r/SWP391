package org.project.converter;

import org.modelmapper.ModelMapper;
import org.project.entity.TestTypeEntity;
import org.project.model.response.TestTypeListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestTypeConverter {
    @Autowired
    private ModelMapper modelMapper;

    public TestTypeListResponse toTestTypeListResponse(TestTypeEntity testTypeEntity) {
        return modelMapper.map(testTypeEntity, TestTypeListResponse.class);
    }
}
