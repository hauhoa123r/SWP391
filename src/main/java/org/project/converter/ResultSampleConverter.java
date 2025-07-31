package org.project.converter;

import jakarta.transaction.Transactional;
import org.project.entity.*;
import org.project.exception.NumberInvalidFormatException;
import org.project.model.dto.ApproveResultDTO;
import org.project.model.response.ResultAppointmentResponse;
import org.project.repository.*;
import org.project.utils.NumberTypeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
@Transactional
public class ResultSampleConverter {

    @Autowired
    private SampleScheduleRepository sampleScheduleRepository;

    @Autowired
    private TestTypeRepository testTypeRepository;

    @Autowired
    private ResultDetailRepository resultDetailRepository;

    @Autowired
    private TestItemRepository testItemRepository;

    @Autowired
    private ReferenceRangeRepository referranceRangeRepositoryImpl;

    @Autowired
    private TestItemRepository testItemRepositoryImpl;

    @Autowired
    private ResultSampleRepository resultRepositoryImpl;

    @Autowired
    private SampleScheduleRepository sampleScheduleRepositoryImpl;

    public ResultEntity toConverterResultEntity(Map<String, String> dataUnit){
        String temp = dataUnit.get("testTypeId");

        Long testTypeId = NumberTypeUtils.convertNumberValueVer2((Object) dataUnit.get("testTypeId"), Long.class);
        Long sampleId = NumberTypeUtils.convertNumberValueVer2((Object) dataUnit.get("sampleId"), Long.class);
        SampleEntity sampleEntity = sampleScheduleRepository.findById(sampleId).get();

        TestTypeEntity testTypeEntity = testTypeRepository.findById(testTypeId).get();
        sampleEntity.setSampleStatus("completed");

        List<TestItemEntity> testItemEntities = testItemRepository.findByTestTypeEntity_Id(testTypeId);

        List<ResultDetailEntity> resultDetailEntities = new ArrayList<>();

        ResultEntity resultEntity = new ResultEntity();
        resultEntity.setSampleEntity(sampleEntity);
        resultEntity.setTestTypeEntity(testTypeEntity);
        resultEntity.setApprovedTime(Instant.now());
        resultEntity.setDataunit(dataUnit.get("symptom"));
        resultEntity.setResultEntryStatus("entered");
        if(setStatusResult(dataUnit)){
            resultEntity.setStatus("Danger");
        }
        else{
            resultEntity.setStatus("Normal");
        }
        resultRepositoryImpl.save(resultEntity);

        testItemEntities.forEach(testItemEntity -> {
            ResultDetailEntity resultDetailEntity = new ResultDetailEntity();
            resultDetailEntity.setResultEntity(resultEntity);
            resultDetailEntity.setTestItemEntity(testItemEntity);

            resultDetailEntity.setValue(BigDecimal.valueOf(Long.parseLong(dataUnit.get(testItemEntity.getName()))));
            resultDetailEntities.add(resultDetailEntity);
            resultDetailRepository.save(resultDetailEntity);
        });
        resultEntity.setResultDetailEntities(resultDetailEntities);
        return resultEntity;
    }


    private boolean setStatusResult(Map<String, String> dataUnit){
        Long testTypeId = NumberTypeUtils.convertNumberValueVer2((Object) dataUnit.get("testTypeId"), Long.class);
        Long sampleId = NumberTypeUtils.convertNumberValueVer2((Object) dataUnit.get("sampleId"), Long.class);

        SampleEntity sampleEntity = sampleScheduleRepositoryImpl.findById(sampleId).get();
        List<TestItemEntity> testItemEntities  = testItemRepositoryImpl.findByTestTypeEntity_Id(testTypeId);

        Set<String> symptoms = new HashSet<>();
        List<String> unitDataDetail = new ArrayList<>();

        long age = converterAgeFromBirthDate(String.valueOf(sampleEntity.getTestRequest().getAppointmentEntity().getPatientEntity().getBirthdate()));
        for (TestItemEntity testItemEntity : testItemEntities) {
            ReferenceRangeEntity referenceRangeEntity = referranceRangeRepositoryImpl.findSymtomPatientFromUnit(testItemEntity.getId(), age);
            long unit = Long.parseLong(dataUnit.get(referenceRangeEntity.getTestItemEntity().getName()));
            String symptom = "";
            String dataDetail = "";
            if (unit < referenceRangeEntity.getMinValue().longValue()) {
                if(referenceRangeEntity.getMinCondition() != null) {
                    symptom += referenceRangeEntity.getMinCondition();
                    dataDetail += "(" + testItemEntity.getName();
                    dataDetail += "): Thấp hơn chỉ số tiêu chuẩn";
                    symptoms.add(symptom);
                    unitDataDetail.add(dataDetail);
                }
                else{
                    symptom += "Chỉ số thấp " + testItemEntity.getName() +" bất thường";
                    symptoms.add(symptom);
                }
            }
            if (unit > referenceRangeEntity.getMaxValue().longValue()) {
                if(referenceRangeEntity.getMaxCondition() != null) {
                    symptom += referenceRangeEntity.getMaxCondition();
                    dataDetail += "(" + testItemEntity.getName();
                    dataDetail += "): Cao hơn chỉ số tiêu chuẩn";
                    symptoms.add(symptom);
                    unitDataDetail.add(dataDetail);
                }else{
                    symptom += "Chỉ số " + testItemEntity.getName() + " cao bất thường";
                    symptoms.add(symptom);
                }
            }

        }
        if(symptoms.size() > 0){
            return true;
        }
        else{
            return false;
        }
    }

    private long converterAgeFromBirthDate(String birthDate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate birth = LocalDate.parse(birthDate, formatter);
        LocalDate now = LocalDate.now();
        return ChronoUnit.YEARS.between(birth, now);
    }

    private long validateConverterException(String number){
        try {
            return Long.parseLong(number);
        }catch (NumberInvalidFormatException e){
            throw new NumberInvalidFormatException("Number is invalid");
        }
    }

    Page<ResultAppointmentResponse> toConverterResultAppointmentResponse(ApproveResultDTO approveResultDTO){
                return null;
    }
}
