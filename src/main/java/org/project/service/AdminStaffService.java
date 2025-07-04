package org.project.service;

import org.project.entity.StaffEntity;
import org.project.model.request.AdminStaffUpdateRequest;
import org.project.model.response.AdminStaffDetailResponse;
import org.project.model.response.AdminStaffResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminStaffService {
    Page<AdminStaffResponse> getAllStaffs(Pageable pageable, String field, String keyword);
    AdminStaffDetailResponse getStaffDetail(Long id);
    StaffEntity getStaffById(Long id); // dùng nội bộ
    AdminStaffUpdateRequest getUpdateForm(Long id);
    void updateStaff(Long id, AdminStaffUpdateRequest request);
}
