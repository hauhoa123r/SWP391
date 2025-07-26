package org.project.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.project.entity.StaffEntity;
import org.project.model.request.DoctorStaffRequest;
import org.project.model.response.DoctorStaffResponse;

@Mapper(componentModel = "spring")
public interface DoctorStaffMapper {

    @Mapping(source = "id", target = "staffId")
    @Mapping(source = "avatarUrl", target = "avatarUrl")
    @Mapping(source = "userEntity.id", target = "userId")
    @Mapping(source = "userEntity.email", target = "email")
    @Mapping(source = "userEntity.phoneNumber", target = "phoneNumber")
    @Mapping(source = "departmentEntity.id", target = "departmentId")
    @Mapping(source = "departmentEntity.name", target = "departmentName")
    @Mapping(source = "hospitalEntity.id", target = "hospitalId")
    @Mapping(source = "hospitalEntity.name", target = "hospitalName")
    @Mapping(source = "manager.id", target = "managerId")
    @Mapping(source = "manager.fullName", target = "managerName")
    @Mapping(source = "doctorEntity.doctorRank", target = "doctorRank")
    DoctorStaffResponse toResponse(StaffEntity entity);

    StaffEntity toEntity(DoctorStaffRequest request);
}