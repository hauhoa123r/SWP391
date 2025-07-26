package org.project.model.response;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.project.entity.StaffEntity;
import org.project.model.request.AdminStaffUpdateRequest;

@Mapper(componentModel = "spring")
public interface AdminStaffMapper {

    // Dùng cho danh sách Staff ngắn gọn (List View)
    @Mapping(source = "id", target = "id")
    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "userEntity.email", target = "email")
    @Mapping(source = "departmentEntity.name", target = "departmentName")
    @Mapping(source = "hospitalEntity.name", target = "hospitalName")
    @Mapping(expression = "java(staffEntity.getManager() != null ? staffEntity.getManager().getFullName() : null)", target = "managerName")
    @Mapping(source = "staffRole", target = "staffRole")
    @Mapping(source = "staffType", target = "staffType")
    @Mapping(source = "rankLevel", target = "rankLevel")
    @Mapping(expression = "java(staffEntity.getAverageRating())", target = "averageRating")
    @Mapping(expression = "java(staffEntity.getReviewCount())", target = "reviewCount")
    @Mapping(source = "staffStatus", target = "status")
    AdminStaffResponse toResponse(StaffEntity staffEntity);

    // Dùng cho chi tiết Staff (Detail View)
    @Mapping(source = "id", target = "id")
    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "userEntity.email", target = "email")
    @Mapping(source = "userEntity.phoneNumber", target = "phoneNumber")
    @Mapping(source = "departmentEntity.name", target = "departmentName")
    @Mapping(source = "hospitalEntity.name", target = "hospitalName")
    @Mapping(expression = "java(staff.getManager() != null ? staff.getManager().getFullName() : null)", target = "managerName")
    @Mapping(source = "staffRole", target = "staffRole")
    @Mapping(source = "staffType", target = "staffType")
    @Mapping(source = "rankLevel", target = "rankLevel")
    @Mapping(source = "avatarUrl", target = "avatarUrl")
    @Mapping(source = "hireDate", target = "hireDate") // Nếu cần định dạng thì định dạng sau trong Thymeleaf
    @Mapping(expression = "java(staff.getAverageRating())", target = "averageRating")
    @Mapping(expression = "java(staff.getReviewCount())", target = "reviewCount")
    AdminStaffDetailResponse toDetailResponse(StaffEntity staff);

    void updateStaffFromRequest(AdminStaffUpdateRequest request, @MappingTarget StaffEntity entity);
}