package org.project.service.impl;

import jakarta.transaction.Transactional;
import org.project.converter.SampleFilterConverter;
import org.project.converter.SampleScheduleConverter;
import org.project.entity.SampleEntity;
import org.project.entity.TestRequestEntity;
import org.project.enums.RequestStatus;
import org.project.model.dto.FilterSampleNameDTO;
import org.project.model.dto.RejectCollectDTO;
import org.project.model.dto.RejectSampleScheduleDTO;
import org.project.model.dto.SampleFilterDTO;
import org.project.model.request.CreateSamplePatientRequest;
import org.project.model.response.ResultSampleResponse;
import org.project.model.response.SampleConfirmResponse;
import org.project.model.response.SampleFilterResponse;
import org.project.model.response.SampleScheduleResponse;
import org.project.repository.AssignmentRepository;
import org.project.repository.SampleScheduleRepository;
import org.project.service.SampleScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.Instant;
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
    @Autowired
    private SampleFilterConverter sampleFilterConverter;
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
        SampleEntity sampleEntity = sampleScheduleRepository.findByTestRequest_Id(rejectSampleScheduleDTO.getTestRequestId());
        sampleEntity.setSampleStatus("rejected");
        sampleScheduleRepository.save(sampleEntity);
        assignmentRepositoryImpl.save(testRequestEntity);
        return true;
    }

    @Override
    public Page<SampleConfirmResponse> getAllSampleConfirm(SampleFilterDTO sampleFilterDTO) throws IllegalAccessException {
        Page<SampleConfirmResponse> sampleConfirmResponses = sampleScheduleConverter.toConvertSampleConfimResponse(sampleFilterDTO);
        return sampleConfirmResponses;
    }

    @Override
    public Boolean approveSampleSchedule(Long id) {
        SampleEntity sampleEntity = sampleScheduleRepository.findById(id).orElseThrow();
        TestRequestEntity testRequestEntity = assignmentRepositoryImpl.findById(sampleEntity.getTestRequest().getId()).orElseThrow();
        testRequestEntity.setRequestStatus(RequestStatus.processing);
        sampleEntity.setSampleStatus("collected");
        sampleEntity.setCollectionTime(Date.from(Instant.now()));
        assignmentRepositoryImpl.save(testRequestEntity);
        sampleScheduleRepository.save(sampleEntity);
        return true;
    }

    @Override
    public Boolean rejectCollect(RejectCollectDTO rejectCollectDTO) {
        SampleEntity sampleEntity = sampleScheduleRepository.findById(rejectCollectDTO.getTestRequestId()).orElseThrow();
        TestRequestEntity testRequestEntity = assignmentRepositoryImpl.findById(sampleEntity.getTestRequest().getId()).orElseThrow();
        testRequestEntity.setRequestStatus(RequestStatus.pending);
        testRequestEntity.setReason(rejectCollectDTO.getReason());
        sampleEntity.setSampleStatus("pending");
        sampleEntity.setRecollectionReason(rejectCollectDTO.getReason());
        assignmentRepositoryImpl.save(testRequestEntity);
        sampleScheduleRepository.save(sampleEntity);
        return true;
    }

    @Override
    public Boolean rejectSampleComfirm(RejectSampleScheduleDTO rejectSampleScheduleDTO) {
        SampleEntity sampleEntity = sampleScheduleRepository.findById(rejectSampleScheduleDTO.getTestRequestId()).orElseThrow();
        TestRequestEntity testRequestEntity = assignmentRepositoryImpl.findById(sampleEntity.getTestRequest().getId()).orElseThrow();
        sampleEntity.setSampleStatus("rejected");
        sampleEntity.setRecollectionReason(rejectSampleScheduleDTO.getReason());
        testRequestEntity.setRequestStatus(RequestStatus.rejected);
        testRequestEntity.setReason(rejectSampleScheduleDTO.getReason());
        assignmentRepositoryImpl.save(testRequestEntity);
        sampleScheduleRepository.save(sampleEntity);
        return true;
    }

    @Override
    public Page<ResultSampleResponse> getAllResultSample(SampleFilterDTO sampleFilterDTO) throws IllegalAccessException {
        Page<ResultSampleResponse> result = sampleScheduleConverter.toConvertResultSampleResponse(sampleFilterDTO);
        return result;
    }

    @Override
    public Page<SampleFilterResponse> searchSampleSchedule(FilterSampleNameDTO filterSampleNameDTO) throws IllegalAccessException {
        Page<SampleFilterResponse> results = sampleFilterConverter.toSampleFilterDTOPage(filterSampleNameDTO);
        return results;
    }
}
