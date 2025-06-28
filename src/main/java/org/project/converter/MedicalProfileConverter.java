package org.project.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.project.entity.MedicalProfileEntity;
import org.project.model.response.MedicalProfileResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;


@Component
public class MedicalProfileConverter {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MedicalProfileResponse toMedicalProfileResponse(MedicalProfileEntity entity) {
        List<String> allergies = parseJsonList(entity.getAllergies());
        List<String> chronicDiseases = parseJsonList(entity.getChronicDiseases());
        return new MedicalProfileResponse(
                entity.getId(),
                allergies,
                chronicDiseases
        );
    }

    private List<String> parseJsonList(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
