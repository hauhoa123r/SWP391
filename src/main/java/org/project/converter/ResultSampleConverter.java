package org.project.converter;

import org.project.entity.ResultEntity;
import org.project.entity.SampleEntity;
import org.project.entity.TestTypeEntity;
import org.project.repository.SampleScheduleRepository;
import org.project.service.TestTypeRepository;
import org.project.utils.NumberTypeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
public class ResultSampleConverter {

    @Autowired
    private SampleScheduleRepository sampleScheduleRepository;

    @Autowired
    private TestTypeRepository testTypeRepository;

    public ResultEntity toConverterResultEntity(Map<String, String> dataUnit){
        String temp = dataUnit.get("testTypeId");

        Long testTypeId = NumberTypeUtils.convertNumberValueVer2((Object) dataUnit.get("testTypeId"), Long.class);
        Long sampleId = NumberTypeUtils.convertNumberValueVer2((Object) dataUnit.get("sampleId"), Long.class);
        SampleEntity sampleEntity = sampleScheduleRepository.findById(sampleId).get();
        TestTypeEntity testTypeEntity = testTypeRepository.findById(testTypeId).get();
        sampleEntity.setSampleStatus("collected");
        ResultEntity resultEntity = new ResultEntity();
        resultEntity.setSampleEntity(sampleEntity);
        resultEntity.setTestTypeEntity(testTypeEntity);
        resultEntity.setApprovedTime(Instant.now());
        resultEntity.setNotes(dataUnit.get("symptom"));
        resultEntity.setResultEntryStatus("entered");
        return resultEntity;
    }
}
