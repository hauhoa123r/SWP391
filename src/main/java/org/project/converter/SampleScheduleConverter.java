package org.project.converter;

import org.project.entity.SampleEntity;
import org.project.entity.TestRequestEntity;
import org.project.entity.UserEntity;
import org.project.exception.ResourceNotFoundException;
import org.project.model.dto.SampleFilterDTO;
import org.project.model.request.CreateSamplePatientRequest;
import org.project.model.response.SampleScheduleResponse;
import org.project.repository.AssignmentRepository;
import org.project.repository.SampleScheduleRepository;
import org.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

@Component
public class SampleScheduleConverter {

    @Autowired
    private SampleScheduleRepository sampleScheduleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    public Page<SampleScheduleResponse> toConvertSampleScheduleResponse(SampleFilterDTO sampleFilterDTO) throws IllegalAccessException {
        Page<SampleEntity> sampleEntityEntities = sampleScheduleRepository.filterSampleEntityCustom(sampleFilterDTO);
        if (sampleEntityEntities == null) {
            throw new ResourceNotFoundException("Cam not get Sample Schedule by search");
        }
        return sampleEntityEntities.map(sampleEntity -> {
            SampleScheduleResponse sampleScheduleResponse = new SampleScheduleResponse();
            sampleScheduleResponse.setId(sampleEntity.getTestRequest().getId());
            sampleScheduleResponse.setPatientName(sampleEntity.getTestRequest().getPatientEntity().getFullName());
            sampleScheduleResponse.setScheduleTime(sampleEntity.getSampleStatus());
            sampleScheduleResponse.setBarCode(sampleEntity.getBarcode());
            sampleScheduleResponse.setTestType(sampleEntity.getTestRequest().getTestType().getTestTypeName());
            return sampleScheduleResponse;
        });
    }

    public SampleEntity createSampleSchedule(CreateSamplePatientRequest createSamplePatientRequest){
        Optional<SampleEntity> sampleEntity = sampleScheduleRepository.findById(createSamplePatientRequest.getSampleId());
        if(!sampleEntity.isPresent()){
            throw new ResourceNotFoundException("Can not find sample by id: " + createSamplePatientRequest.getSampleId());
        }
        String dateStr = createSamplePatientRequest.getDate();
        String timeStr = createSamplePatientRequest.getTime();

        String dateTimeStr = dateStr + " " + timeStr + ":00";

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, format);
        Date scheduledTime = java.sql.Date.valueOf(dateTime.toLocalDate());

        Optional<UserEntity> userEntity = userRepository.findById(createSamplePatientRequest.getManagerId());
        Optional<TestRequestEntity> testRequestEntity = assignmentRepository.findById(createSamplePatientRequest.getTestRequestId());
        sampleEntity.get().setSampler(userEntity.get());
        sampleEntity.get().setCollectionTime(scheduledTime);
        sampleEntity.get().setSampleStatus("received");
        sampleEntity.get().setTestRequest(testRequestEntity.get());
        sampleEntity.get().setNotes(createSamplePatientRequest.getNote());
        return sampleEntity.get();
    }
}
