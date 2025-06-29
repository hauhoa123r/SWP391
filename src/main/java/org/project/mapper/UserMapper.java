package org.project.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.project.entity.PatientEntity;
import org.project.entity.UserEntity;
import org.project.model.response.UserListResponse;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Mapping từ UserEntity + PatientEntity → UserListResponse
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "phoneNumber", source = "user.phoneNumber")
    @Mapping(target = "userRole", source = "user.userRole")
    @Mapping(target = "userStatus", source = "user.userStatus")
    @Mapping(target = "isVerified", source = "user.isVerified")
    @Mapping(target = "twoFactorEnabled", source = "user.twoFactorEnabled")
    @Mapping(target = "fullName", source = "patient.fullName")
    @Mapping(target = "avatarUrl", source = "patient.avatarUrl")
    @Mapping(target = "address", source = "patient.address")
    @Mapping(target = "birthdate", source = "patient.birthdate")
    @Mapping(target = "gender", source = "patient.gender")
    @Mapping(target = "familyRelationship", source = "patient.familyRelationship")
    @Mapping(target = "bloodType", source = "patient.bloodType")
    UserListResponse toUserListResponse(UserEntity user, PatientEntity patient);

    // Fallback nếu user không có patient
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "phoneNumber", source = "user.phoneNumber")
    @Mapping(target = "userRole", source = "user.userRole")
    @Mapping(target = "userStatus", source = "user.userStatus")
    @Mapping(target = "isVerified", source = "user.isVerified")
    @Mapping(target = "twoFactorEnabled", source = "user.twoFactorEnabled")
    @Mapping(target = "fullName", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "birthdate", ignore = true)
    @Mapping(target = "gender", ignore = true)
    @Mapping(target = "familyRelationship", ignore = true)
    @Mapping(target = "bloodType", ignore = true)
    UserListResponse toUserListResponse(UserEntity user);
}
