package org.project.service.impl;

import jakarta.transaction.Transactional;
import org.project.converter.ResultSampleConverter;
import org.project.converter.ResultTestConverter;
import org.project.entity.ResultEntity;
import org.project.model.dto.ResultTestDTO;
import org.project.model.response.ResultAppointmentResponse;
import org.project.repository.ResultSampleRepository;
import org.project.service.ResultSampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Transactional
public class ResultSampleServiceImpl implements ResultSampleService {
    @Autowired
    private ResultSampleConverter resultSampleConverter;
    @Autowired
    private ResultSampleRepository resultSampleRepository;
    @Autowired
    private ResultTestConverter resultTestConverter;

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
}
