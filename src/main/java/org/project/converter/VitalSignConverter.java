package org.project.converter;

import org.modelmapper.ModelMapper;
import org.project.entity.VitalSignEntity;
import org.project.model.response.VitalSignResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VitalSignConverter {
    @Autowired
    private ModelMapper modelMapper;

    public VitalSignResponse toVitalSignResponse(VitalSignEntity vitalSignEntity) {
        return modelMapper.map(vitalSignEntity, VitalSignResponse.class);
    }
}
