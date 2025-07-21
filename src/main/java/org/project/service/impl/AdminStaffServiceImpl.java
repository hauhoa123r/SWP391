package org.project.service.impl;

import lombok.RequiredArgsConstructor;
import org.project.entity.StaffEntity;
import org.project.enums.StaffStatus;
import org.project.exception.ResourceNotFoundException;
import org.project.exception.sql.EntityNotFoundException;
import org.project.model.request.AdminStaffUpdateRequest;
import org.project.model.response.AdminStaffDetailResponse;
import org.project.model.response.AdminStaffMapper;
import org.project.model.response.AdminStaffResponse;
import org.project.repository.DepartmentRepository;
import org.project.repository.HospitalRepository;
import org.project.repository.StaffRepository;
import org.project.service.AdminStaffService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminStaffServiceImpl implements AdminStaffService {

    private final StaffRepository staffRepository;
    private final AdminStaffMapper staffMapper;
    private final DepartmentRepository departmentRepository;
    private final HospitalRepository hospitalRepository;

    // ===== Get all staffs (including search) =====
    @Override
    public Page<AdminStaffResponse> getAllStaffs(Pageable pageable, String field, String keyword) {
        Page<StaffEntity> staffPage = staffRepository.searchStaffs(pageable, field, keyword);
        return staffPage.map(staffMapper::toResponse);
    }

    @Override
    public AdminStaffDetailResponse getStaffDetail(Long id) {
        StaffEntity staff = getStaffById(id);
        return staffMapper.toDetailResponse(staff);
    }

    @Override
    public StaffEntity getStaffById(Long id) {
        return staffRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Staff not found"));
    }

    // ===== Update Form Mapping =====
    @Override
    public AdminStaffUpdateRequest getUpdateForm(Long id) {
        StaffEntity staff = getStaffById(id);
        AdminStaffUpdateRequest req = new AdminStaffUpdateRequest();
        req.setFullName(staff.getFullName());
        req.setDepartmentId(staff.getDepartmentEntity().getId());
        req.setHospitalId(staff.getHospitalEntity().getId());
        req.setManagerId(staff.getStaffEntity() != null ? staff.getStaffEntity().getId() : null);
        req.setRankLevel(staff.getRankLevel());
        req.setAvatarUrl(staff.getAvatarUrl());
        req.setStaffRole(staff.getStaffRole().name());
        req.setStaffType(staff.getStaffType().name());
        return req;
    }

    @Override
    public void updateStaff(Long id, AdminStaffUpdateRequest request) {
        StaffEntity staff = getStaffById(id);
        staffMapper.updateStaffFromRequest(request, staff);

        staff.setDepartmentEntity(departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found")));
        staff.setHospitalEntity(hospitalRepository.findById(request.getHospitalId())
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found")));

        if (request.getManagerId() != null) {
            staff.setStaffEntity(staffRepository.findById(request.getManagerId()).orElse(null));
        } else {
            staff.setStaffEntity(null);
        }

        staffRepository.save(staff);
    }

    // ===== Soft Delete =====
    @Override
    public void deactivateStaff(Long id) {
        StaffEntity staff = getStaffById(id);
        staff.setStaffStatus(StaffStatus.INACTIVE);
        staffRepository.save(staff);
    }

    @Override
    public void restoreStaff(Long id) {
        StaffEntity staff = getStaffById(id);
        staff.setStaffStatus(StaffStatus.ACTIVE);
        staffRepository.save(staff);
    }

    @Override
    public Page<AdminStaffResponse> getDeletedStaffs(Pageable pageable) {
        return staffRepository.findByStaffStatus(StaffStatus.INACTIVE, pageable)
                .map(staffMapper::toResponse);
    }

    // ===== Hard Delete =====
    @Override
    public void deleteStaffPermanently(Long id) {
        StaffEntity staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found with id: " + id));
        staffRepository.delete(staff); // xóa vĩnh viễn
    }
    // ===== Search theo từng trường =====
    @Override
    public Page<AdminStaffResponse> searchByFullName(String keyword, Pageable pageable) {
        return staffRepository.searchStaffs(pageable, "fullName", keyword)
                .map(staffMapper::toResponse);
    }

    @Override
    public Page<AdminStaffResponse> searchByEmail(String keyword, Pageable pageable) {
        return staffRepository.searchStaffs(pageable, "email", keyword)
                .map(staffMapper::toResponse);
    }

    @Override
    public Page<AdminStaffResponse> searchByPhoneNumber(String keyword, Pageable pageable) {
        return staffRepository.searchStaffs(pageable, "phoneNumber", keyword)
                .map(staffMapper::toResponse);
    }
}
