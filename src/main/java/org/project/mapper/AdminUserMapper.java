package org.project.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.project.entity.PatientEntity;
import org.project.entity.UserEntity;
import org.project.model.dto.UserPatientDTO;

@Mapper(componentModel = "spring")
public interface AdminUserMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.phoneNumber", target = "phoneNumber")
    @Mapping(source = "user.userRole", target = "userRole")
    @Mapping(source = "user.userStatus", target = "userStatus")
    @Mapping(source = "user.isVerified", target = "isVerified")
    @Mapping(source = "user.twoFactorEnabled", target = "twoFactorEnabled")

    @Mapping(source = "patient.fullName", target = "fullName")
    @Mapping(source = "patient.avatarUrl", target = "avatarUrl")
    @Mapping(source = "patient.address", target = "address")
    @Mapping(source = "patient.birthdate", target = "birthdate")
    @Mapping(source = "patient.gender", target = "gender")
    @Mapping(source = "patient.familyRelationship", target = "familyRelationship")
    @Mapping(source = "patient.bloodType", target = "bloodType")

    UserPatientDTO toUserPatientDTO(UserEntity user, PatientEntity patient);
}
