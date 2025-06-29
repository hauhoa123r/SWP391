package org.project.converter;

import org.modelmapper.ModelMapper;
import org.project.entity.MedicalRecordSymptomEntity;
import org.project.model.response.MedicalRecordSymptomResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MedicalRecordSymptomConverter {
    @Autowired
    private ModelMapper modelMapper;

    public MedicalRecordSymptomResponse toMedicalRecordSymptomResponse(MedicalRecordSymptomEntity medicalRecordSymptomEntity) {
        return modelMapper.map(medicalRecordSymptomEntity, MedicalRecordSymptomResponse.class);
    }
}
