package org.project.service.impl;

import org.project.entity.AppointmentEntity;
import org.project.entity.TestRequestEntity;
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
    @Override
    public Long createTestRequest(Long appointmentId) {
        TestRequestEntity testRequestEntity = new TestRequestEntity();
        AppointmentEntity appointmentEntity = appointmentRepository.findById(appointmentId).get();
        testRequestEntity.setAppointmentEntity(appointmentEntity);
        testRequestRepository.save(testRequestEntity);
        return testRequestEntity.getId();
    }
}
