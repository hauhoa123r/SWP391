//package org.project.mapper;
//
//import org.mapstruct.*;
//import org.project.dto.AddUserRequest;
//import org.project.dto.AddUserResponse;
//import org.project.entity.PatientEntity;
//import org.project.entity.StaffEntity;
//import org.project.entity.UserEntity;
//import org.springframework.stereotype.Component;
//
//@Mapper(componentModel = "spring")
//public interface AddUserMapper {
//    PatientEntity toPatient(AddUserRequest.PatientInfo dto);
//    StaffEntity toStaff(AddUserRequest.StaffInfo dto);
//
//    @Mapping(source = "id", target = "userId")
//    @Mapping(source = "userRole", target = "role")
//    @Mapping(target = "patientId", expression = "java(patient != null ? patient.getId() : null)")
//    @Mapping(target = "staffId", expression = "java(staff != null ? staff.getId() : null)")
//    AddUserResponse toResponse(UserEntity user, PatientEntity patient, StaffEntity staff);
//}
