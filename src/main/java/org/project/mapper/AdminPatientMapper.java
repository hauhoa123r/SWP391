package org.project.mapper;
//
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.project.entity.PatientEntity;
import org.project.model.response.AdminPatientResponse;
//
import java.util.List;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
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
    @Mapping(target = "dateOfBirth", source = "birthdate", qualifiedByName = "dateToLocalDate")
    @Mapping(target = "familyRelationship", source = "familyRelationship", qualifiedByName = "enumToString")
    @Mapping(target = "bloodType", source = "bloodType")
    @Mapping(target = "age", source = "birthdate", qualifiedByName = "calculateAge")
    //@Mapping(target = "medicalProfileEntity", source = "medicalProfileEntity")
    AdminPatientResponse toResponse(PatientEntity entity);
//
    List<AdminPatientResponse> toResponseList(List<PatientEntity> entities);

    @Named("dateToLocalDate")
    default LocalDate mapDateToLocalDate(Date date) {
        return date != null ? date.toLocalDate() : null;
    }

    @Named("enumToString")
    default String mapEnumToString(Enum<?> e) {
        return e != null ? e.name() : null;
    }

    @Named("calculateAge")
    default Integer calculateAge(Date date) {
        return date != null ? Period.between(date.toLocalDate(), LocalDate.now()).getYears() : null;
    }
}
