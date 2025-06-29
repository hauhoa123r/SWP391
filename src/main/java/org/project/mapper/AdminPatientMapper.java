package org.project.mapper;
//
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.project.entity.PatientEntity;
import org.project.model.response.AdminPatientResponse;
//
import java.util.List;
//
@Mapper(componentModel = "spring")
public interface AdminPatientMapper {
//
    @Mapping(target = "id", source = "id")
    @Mapping(target = "fullName", source = "fullName")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "gender", source = "gender")
    @Mapping(target = "dateOfBirth", expression = "java(entity.getBirthdate() != null ? entity.getBirthdate().toLocalDate() : null)")
    @Mapping(target = "familyRelationship", expression = "java(entity.getFamilyRelationship() != null ? entity.getFamilyRelationship().name() : null)")
    @Mapping(target = "bloodType", source = "bloodType")
    //@Mapping(target = "medicalProfileEntity", source = "medicalProfileEntity")
    AdminPatientResponse toResponse(PatientEntity entity);
//
    List<AdminPatientResponse> toResponseList(List<PatientEntity> entities);
}
