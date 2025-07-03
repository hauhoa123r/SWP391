package org.project.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.project.entity.StaffEntity;
import org.project.model.request.DoctorStaffRequest;
import org.project.model.response.DoctorStaffResponse;

@Mapper(componentModel = "spring")
public interface DoctorStaffMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.phoneNumber", target = "phoneNumber")

    @Mapping(source = "department.id", target = "departmentId")
    @Mapping(source = "department.name", target = "departmentName")

    @Mapping(source = "hospital.id", target = "hospitalId")
    @Mapping(source = "hospital.name", target = "hospitalName")

    @Mapping(source = "manager.id", target = "managerId")
    @Mapping(source = "manager.fullName", target = "managerName")
    DoctorStaffResponse toResponse(StaffEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "department.id", source = "departmentId")
    @Mapping(target = "hospital.id", source = "hospitalId")
    @Mapping(target = "manager.id", source = "managerId")
    StaffEntity toEntity(DoctorStaffRequest request);
}

