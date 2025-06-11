package org.project.converter;

import org.aspectj.weaver.ast.Test;
import org.modelmapper.ModelMapper;
import org.project.entity.TestEntity;
import org.project.model.response.TestListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestConverter {
    @Autowired
    private ModelMapper modelMapper;

    public TestListResponse toTestListResponse(TestEntity testEntity) {
        return modelMapper.map(testEntity, TestListResponse.class);
    }
}
