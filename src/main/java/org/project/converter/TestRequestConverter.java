package org.project.converter;

import org.modelmapper.ModelMapper;
import org.project.entity.TestRequestEntity;
import org.project.model.response.TestRequestInAppointment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestRequestConverter {
    @Autowired
    private ModelMapper modelMapper;

    public TestRequestInAppointment convertToTestRequestInAppointment(TestRequestEntity testRequestEntity) {
        return modelMapper.map(testRequestEntity, TestRequestInAppointment.class);
    }
}
