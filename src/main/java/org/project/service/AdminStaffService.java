package org.project.service;

import org.project.entity.StaffEntity;
import org.project.model.request.AdminStaffUpdateRequest;
import org.project.model.response.AdminStaffDetailResponse;
import org.project.model.response.AdminStaffResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminStaffService {
    Page<AdminStaffResponse> getAllStaffs(Pageable pageable, String field, String keyword);

    // search by individual field
    Page<AdminStaffResponse> searchByFullName(String keyword, Pageable pageable);
    Page<AdminStaffResponse> searchByEmail(String keyword, Pageable pageable);
    Page<AdminStaffResponse> searchByPhoneNumber(String keyword, Pageable pageable);
    AdminStaffDetailResponse getStaffDetail(Long id);
    StaffEntity getStaffById(Long id); // dùng nội bộ
    AdminStaffUpdateRequest getUpdateForm(Long id);
    void updateStaff(Long id, AdminStaffUpdateRequest request);
    
    // Soft delete functionality
    void softDeleteStaff(Long id);
    void restoreStaff(Long id);
    void deletePermanently(Long id);
    Page<AdminStaffResponse> getDeletedStaffs(Pageable pageable);

}
