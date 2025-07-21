package org.project.service.impl;

import org.project.converter.TestRequestConverter;
import org.project.entity.AppointmentEntity;
import org.project.entity.TestRequestEntity;
import org.project.enums.RequestStatus;
import org.project.model.request.TestListRequest;
import org.project.model.response.TestRequestInAppointment;
import org.project.repository.AppointmentVRepository;
import org.project.repository.TestRequestRepository;
import org.project.repository.TestTypeRepository;
import org.project.service.TestRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TestRequestServiceImpl implements TestRequestService {
    @Autowired
    private TestRequestRepository testRequestRepository;
    @Autowired
    private AppointmentVRepository appointmentRepository;
    @Autowired
    private TestTypeRepository testTypeRepository;
    @Autowired
    private TestRequestConverter testRequestConverter;
    @Override
    public Boolean addListTestRequestByAppointmentId(TestListRequest testListRequest) {
        AppointmentEntity appointmentEntity = appointmentRepository.findById(testListRequest.getAppointmentId()).orElse(null);
        System.out.println(appointmentEntity);
        List<TestRequestInAppointment> testRequestInAppointment = new ArrayList<>();
        for(Long testTypeId : testListRequest.getTestTypeId()){
            TestRequestEntity testRequest = TestRequestEntity.builder()
                    .appointmentEntity(appointmentEntity)
                    .patientEntity(appointmentEntity.getPatientEntity())
                    .doctorEntity(appointmentEntity.getDoctorEntity())
                    .testTypeEntity(testTypeRepository.findById(testTypeId).orElse(null))
                    .requestStatus(RequestStatus.pending)
                    .requestTime(LocalDate.now())
                    .build();
            System.out.println(testRequest);
            testRequestRepository.save(testRequest);
            testRequestInAppointment.add(testRequestConverter.convertToTestRequestInAppointment(testRequest));
        }
        return true;
    }

    @Override
    public List<TestRequestInAppointment> getListTestRequest(Long appointmentId) {
        List<TestRequestEntity> testRequestEntities = testRequestRepository.findByAppointmentEntityId(appointmentId);
        return testRequestEntities.stream().map(testRequestConverter::convertToTestRequestInAppointment).toList();
    }

    @Override
    public Boolean deleteTestRequest(Long testRequestId) {
        testRequestRepository.deleteById(testRequestId);
        return true;
    }
}
