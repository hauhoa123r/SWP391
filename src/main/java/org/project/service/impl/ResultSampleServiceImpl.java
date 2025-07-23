package org.project.service.impl;

import jakarta.transaction.Transactional;
import org.project.converter.ResultSampleConverter;
import org.project.entity.ResultEntity;
import org.project.repository.ResultSampleRepository;
import org.project.service.ResultSampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Transactional
public class ResultSampleServiceImpl implements ResultSampleService {
    @Autowired
    private ResultSampleConverter resultSampleConverter;
    @Autowired
    private ResultSampleRepository resultSampleRepository;


    @Override
    public ResultEntity isSaveResultSample(Map<String, String> dataUnit) {
        ResultEntity resultEntity = resultSampleConverter.toConverterResultEntity( dataUnit);
        resultSampleRepository.save(resultEntity);
        return resultEntity;
    }
}
