package org.project.service.impl;

import jakarta.transaction.Transactional;
import org.project.converter.ResultSampleConverter;
import org.project.converter.ResultTestConverter;
import org.project.entity.AppointmentEntity;
import org.project.entity.ResultEntity;
import org.project.entity.SampleEntity;
import org.project.entity.TestRequestEntity;
import org.project.enums.RequestStatus;
import org.project.model.dto.ResultTestDTO;
import org.project.model.response.ResultAppointmentResponse;
import org.project.repository.AppointmentRepository;
import org.project.repository.ResultSampleRepository;
import org.project.repository.SampleScheduleRepository;
import org.project.repository.TestRequestRepository;
import org.project.service.ResultSampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class ResultSampleServiceImpl implements ResultSampleService {
    @Autowired
    private ResultSampleConverter resultSampleConverter;
    @Autowired
    private ResultSampleRepository resultSampleRepository;
    @Autowired
    private ResultTestConverter resultTestConverter;
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private SampleScheduleRepository sampleScheduleRepository;
    @Autowired
    private TestRequestRepository testRequestRepository;
    @Override
    public ResultEntity isSaveResultSample(Map<String, String> dataUnit) {
        ResultEntity resultEntity = resultSampleConverter.toConverterResultEntity( dataUnit);
        resultSampleRepository.save(resultEntity);
        return resultEntity;
    }

    @Override
    public Page<ResultAppointmentResponse> filterResultSample(ResultTestDTO resultTestDTO) throws IllegalAccessException {
        Page<ResultAppointmentResponse> resultAppointmentResponses = resultTestConverter.filterResultSample(resultTestDTO);
        return resultAppointmentResponses;
    }

    @Override
    public Boolean approveResultSample(Long id) {
        Optional<AppointmentEntity> appointmentEntity = appointmentRepository.findById(id);
        if(! appointmentEntity.isPresent() ){
            return false;
        }

        appointmentEntity.get().getTestRequestEntities().forEach(testRequestEntity -> {
            TestRequestEntity testRequestEntity1 = testRequestEntity;
            SampleEntity sampleEntity = new SampleEntity();
            if(testRequestEntity.getSamples() != null && testRequestEntity.getSamples().getSampleStatus().equals("collected")){
                sampleEntity = testRequestEntity.getSamples();
                sampleEntity.setSampleStatus("completed");
                sampleScheduleRepository.save(sampleEntity);
                testRequestEntity1.setRequestStatus(RequestStatus.completed);
                testRequestRepository.save(testRequestEntity1);
            }
        });
        appointmentEntity.get().setResultUrl("/lab/view-result-patient/" + appointmentEntity.get().getId());
        appointmentRepository.save(appointmentEntity.get());
        return true;
    }

}
