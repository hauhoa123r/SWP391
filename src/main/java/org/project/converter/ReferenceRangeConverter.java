package org.project.converter;


import org.project.entity.ReferenceRangeEntity;
import org.project.entity.SampleEntity;
import org.project.entity.TestItemEntity;
import org.project.exception.NumberInvalidFormatException;
import org.project.model.response.SymtomResponse;
import org.project.repository.ReferenceRangeRepository;
import org.project.repository.SampleScheduleRepository;
import org.project.repository.TestItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
public class ReferenceRangeConverter {

    @Autowired
    private ReferenceRangeRepository referranceRangeRepositoryImpl;

    @Autowired
    private TestItemRepository testItemRepositoryImpl;

    @Autowired
    private SampleScheduleRepository sampleScheduleRepositoryImpl;

    public SymtomResponse getSymtomResponse(Map<String, String> dataUnit) {
        long sampleId = validateConverterException(dataUnit.get("sampleId"));
        long testTypeId = validateConverterException(dataUnit.get("testTypeId"));

        SymtomResponse symtomResponse = new SymtomResponse();
        SampleEntity sampleEntity = sampleScheduleRepositoryImpl.findById(sampleId).get();
        Set<String> symptoms = new HashSet<>();
        List<String> unitDataDetail = new ArrayList<>();
        List<TestItemEntity> testItemEntities  = testItemRepositoryImpl.findByTestTypeEntity_Id(testTypeId);
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
            symtomResponse.setStatus("Danger");
        }
        else{
            symtomResponse.setStatus("Safe");
            unitDataDetail.add("Ổn định");
        }
        symtomResponse.setSymtomText(symptoms.stream().toList());
        symtomResponse.setUnitDataDetails(unitDataDetail.stream().toList());
        return symtomResponse;
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
}
