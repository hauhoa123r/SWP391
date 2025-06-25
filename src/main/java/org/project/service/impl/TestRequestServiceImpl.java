package org.project.service.impl;

import org.project.converter.TestRequestConverter;
import org.project.entity.AppointmentEntity;
import org.project.entity.TestRequestEntity;
import org.project.model.response.TestRequestResponse;
import org.project.repository.AppointmentRepository;
import org.project.repository.TestRequestRepository;
import org.project.service.TestRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestRequestServiceImpl implements TestRequestService {
    @Autowired
    private TestRequestRepository testRequestRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private TestRequestConverter testRequestConverter;
    @Override
    public Long createTestRequest(Long appointmentId) {
        TestRequestEntity testRequestEntity = new TestRequestEntity();
        AppointmentEntity appointmentEntity = appointmentRepository.findById(appointmentId).get();
        testRequestEntity.setAppointmentEntity(appointmentEntity);
        return testRequestRepository.save(testRequestEntity).getId();
    }

    @Override
    public TestRequestResponse getTestRequest(Long appointmentId) {
        TestRequestEntity testRequestEntity = testRequestRepository.findById(appointmentId).get();
        return testRequestConverter.toTestRequestResponse(testRequestEntity);
    }

    public static void main(String[] args) {
        TestRequestServiceImpl testRequestService = new TestRequestServiceImpl();
        TestRequestResponse testRequestResponse = testRequestService.getTestRequest(13L);
        System.out.println(testRequestResponse);
    }

}
