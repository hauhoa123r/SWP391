package org.project.mapper;

import org.project.entity.PatientEntity;
import org.project.model.response.AdminPatientResponse;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manual implementation of {@link AdminPatientMapper} to avoid MapStruct dependency issues.
 * <p>
 * Once annotation processing is correctly configured, this class can be removed and
 * MapStruct will generate an implementation automatically.
 */
@Component
public class AdminPatientMapperImpl implements AdminPatientMapper {

    @Override
    public AdminPatientResponse toResponse(PatientEntity entity) {
        if (entity == null) {
            return null;
        }
        AdminPatientResponse dto = new AdminPatientResponse();
        dto.setId(entity.getId());
        dto.setFullName(entity.getFullName());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setEmail(entity.getEmail());
        dto.setAddress(entity.getAddress());
        dto.setGender(entity.getGender());
        dto.setFamilyRelationship(entity.getFamilyRelationship() != null ? entity.getFamilyRelationship().name() : null);
        dto.setBloodType(entity.getBloodType());

        Date birthdate = entity.getBirthdate();
        LocalDate date = mapDateToLocalDate(birthdate);
        dto.setDateOfBirth(date);
        dto.setAge(calculateAge(birthdate));
        return dto;
    }

    @Override
    public List<AdminPatientResponse> toResponseList(List<PatientEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        return entities.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // Reuse default methods from interface
    @Override
    public LocalDate mapDateToLocalDate(Date date) {
        return AdminPatientMapper.super.mapDateToLocalDate(date);
    }

    @Override
    public String mapEnumToString(Enum<?> e) {
        return AdminPatientMapper.super.mapEnumToString(e);
    }

    @Override
    public Integer calculateAge(Date date) {
        return AdminPatientMapper.super.calculateAge(date);
    }
}
