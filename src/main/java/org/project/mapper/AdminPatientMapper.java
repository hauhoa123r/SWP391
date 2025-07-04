package org.project.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.project.entity.PatientEntity;
import org.project.model.request.AdminPatientUpdateRequest;
import org.project.model.response.AdminPatientDetailResponse;
import org.project.model.response.AdminPatientResponse;

@Mapper(componentModel = "spring")
public interface AdminPatientMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    @Mapping(source = "address", target = "address")
    @Mapping(target = "dateOfBirth", expression = "java(patient.getBirthdate().toLocalDate())")
    @Mapping(target = "age", expression = "java(java.time.Period.between(patient.getBirthdate().toLocalDate(), java.time.LocalDate.now()).getYears())")
    @Mapping(source = "gender", target = "gender")
    @Mapping(source = "familyRelationship", target = "familyRelationship")
    @Mapping(source = "bloodType", target = "bloodType")
    AdminPatientResponse toResponse(PatientEntity patient);

    AdminPatientDetailResponse toDetailResponse(PatientEntity patient);

    AdminPatientUpdateRequest toUpdateRequest(PatientEntity patient);

    void updatePatientFromRequest(AdminPatientUpdateRequest request, @MappingTarget PatientEntity entity);
}
