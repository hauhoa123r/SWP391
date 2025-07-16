package org.project.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.project.entity.DepartmentEntity;
import org.project.entity.HospitalEntity;
import org.project.entity.StaffEntity;
import org.project.entity.UserEntity;
import org.project.enums.StaffType;
import org.project.model.request.DoctorStaffRequest;
import org.project.model.response.DoctorStaffResponse;
@Mapper(componentModel = "spring")
public interface DoctorStaffMapper {

    @Mapping(source = "id", target = "staffId")
    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "staffRole", target = "staffRole")
    @Mapping(source = "staffType", target = "staffType")
    @Mapping(source = "rankLevel", target = "rankLevel")
    @Mapping(source = "hireDate", target = "hireDate", dateFormat = "yyyy-MM-dd")
    @Mapping(source = "avatarUrl", target = "avatarUrl")

    // User
    @Mapping(source = "userEntity.id", target = "userId")
    @Mapping(source = "userEntity.email", target = "email")
    @Mapping(source = "userEntity.phoneNumber", target = "phoneNumber")

    // Department
    @Mapping(source = "departmentEntity.id", target = "departmentId")
    @Mapping(source = "departmentEntity.name", target = "departmentName")

    // Hospital
    @Mapping(source = "hospitalEntity.id", target = "hospitalId")
    @Mapping(source = "hospitalEntity.name", target = "hospitalName")

    // Manager
    @Mapping(source = "staffEntity.id", target = "managerId")
    @Mapping(source = "staffEntity.fullName", target = "managerName")

    // Doctor info
    @Mapping(source = "doctorEntity.doctorRank", target = "doctorRank")
    DoctorStaffResponse toResponse(StaffEntity entity);

    StaffEntity toEntity(DoctorStaffRequest request);
}
