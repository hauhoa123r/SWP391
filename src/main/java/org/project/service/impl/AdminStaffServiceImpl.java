package org.project.service.impl;

import lombok.RequiredArgsConstructor;
import org.project.entity.StaffEntity;
import org.project.exception.sql.EntityNotFoundException;
import org.project.model.request.AdminStaffUpdateRequest;
import org.project.model.response.AdminStaffDetailResponse;
import org.project.model.response.AdminStaffResponse;
import org.project.model.response.AdminStaffMapper;
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

    @Override
    public Page<AdminStaffResponse> getAllStaffs(Pageable pageable, String field, String keyword) {
        Page<StaffEntity> staffPage = staffRepository.searchStaffs(pageable, field, keyword);
        return staffPage.map(staffMapper::toResponse);
    }
    @Override
    public AdminStaffDetailResponse getStaffDetail(Long id) {
        StaffEntity staff = staffRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Staff not found"));
        return staffMapper.toDetailResponse(staff);
    }
    @Override
    public StaffEntity getStaffById(Long id) {
        return staffRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Staff not found"));
    }

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
        // cần fetch entity liên quan trước khi set
        staff.setDepartmentEntity(departmentRepository.findById(request.getDepartmentId()).orElseThrow());
        staff.setHospitalEntity(hospitalRepository.findById(request.getHospitalId()).orElseThrow());
        if (request.getManagerId() != null) {
            staff.setStaffEntity(staffRepository.findById(request.getManagerId()).orElse(null));
        } else {
            staff.setStaffEntity(null);
        }
        staffRepository.save(staff);
    }
}
