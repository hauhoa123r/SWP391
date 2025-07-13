package org.project.service.impl;

import jakarta.transaction.Transactional;
import org.project.converter.SampleScheduleConverter;
import org.project.entity.SampleEntity;
import org.project.entity.TestRequestEntity;
import org.project.enums.RequestStatus;
import org.project.model.dto.RejectSampleScheduleDTO;
import org.project.model.dto.SampleFilterDTO;
import org.project.model.request.CreateSamplePatientRequest;
import org.project.model.response.SampleScheduleResponse;
import org.project.repository.AssignmentRepository;
import org.project.repository.SampleScheduleRepository;
import org.project.service.SampleScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@Transactional
public class SampleScheduleServiceImpl implements SampleScheduleService {

    @Autowired
    private SampleScheduleConverter sampleScheduleConverter;
    @Autowired
    private SampleScheduleRepository sampleScheduleRepository;
    @Autowired
    private AssignmentRepository assignmentRepositoryImpl;

    @Override
    public Page<SampleScheduleResponse> getAllSampleSchedule(SampleFilterDTO sampleFilterDTO) throws IllegalAccessException {
        Page<SampleScheduleResponse> results = sampleScheduleConverter.toConvertSampleScheduleResponse(sampleFilterDTO);
        return results;
    }

    @Override
    public Boolean createSampleSchedule(CreateSamplePatientRequest createSamplePatientRequest) {
        SampleEntity sampleEntity = sampleScheduleConverter.createSampleSchedule(createSamplePatientRequest);
        sampleScheduleRepository.save(sampleEntity);
        return true;
    }

    @Override
    public Boolean rejectSampleSchedule(RejectSampleScheduleDTO rejectSampleScheduleDTO) {
        TestRequestEntity testRequestEntity = assignmentRepositoryImpl
                .findById(rejectSampleScheduleDTO.getTestRequestId())
                .orElseThrow();
        testRequestEntity.setReason(rejectSampleScheduleDTO.getReason());
        testRequestEntity.setRequestTime(Date.from(Instant.now()));
        testRequestEntity.setRequestStatus(RequestStatus.rejected);
        assignmentRepositoryImpl.save(testRequestEntity);
        return true;
    }
}
