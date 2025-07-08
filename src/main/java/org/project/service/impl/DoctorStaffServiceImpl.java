
package org.project.service.impl;

import lombok.RequiredArgsConstructor;
import org.project.entity.*;
import org.project.enums.DoctorRank;
import org.project.mapper.DoctorStaffMapper;
import org.project.model.request.DoctorStaffRequest;
import org.project.model.response.DoctorStaffResponse;
import org.project.repository.*;
import org.project.service.DoctorStaffService;
import org.project.validation.DoctorStaffValidator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class DoctorStaffServiceImpl implements DoctorStaffService {

    private final StaffRepository staffRepo;
    private final UserRepository userRepo;
    private final DepartmentRepository deptRepo;
    private final HospitalRepository hospitalRepo;
    private final DoctorRepository doctorRepo;
    private final DoctorStaffMapper mapper;
    private final DoctorStaffValidator validator;

    @Override
    public DoctorStaffResponse createDoctorStaff(DoctorStaffRequest request) {
        // Validate đầu vào
        validator.validate(request);

        // Ánh xạ request sang entity Staff
        StaffEntity entity = mapper.toEntity(request);

        // ================================
        // 1. Tạo User nếu chưa có
        // ================================
        if (request.getUserId() == null) {
            UserEntity user = new UserEntity();
            user.setEmail(request.getEmail());
            user.setPhoneNumber(request.getPhoneNumber());
            userRepo.save(user);
            entity.setUserEntity(user);
        } else {
            entity.setUserEntity(userRepo.findById(request.getUserId()).orElse(null));
        }

        // ================================
        // 2. Thiết lập phòng ban (nếu có)
        // ================================
        DepartmentEntity dept = (request.getDepartmentId() != null)
                ? deptRepo.findById(request.getDepartmentId()).orElse(null)
                : null; // có thể bỏ chọn
        entity.setDepartmentEntity(dept);

        // ================================
        // 3. Thiết lập bệnh viện (bắt buộc)
        // ================================
        if (request.getHospitalId() != null) {
            HospitalEntity hospital = hospitalRepo.findById(request.getHospitalId()).orElse(null);
            entity.setHospitalEntity(hospital);

            // ================================
            // 4. Gán quản lý tự động nếu chưa có
            // ================================
            if (dept != null && entity.getStaffEntity() == null) {
                staffRepo.findFirstByDepartmentEntity_IdAndHospitalEntity_IdOrderByRankLevelAsc(
                        dept.getId(), hospital.getId()
                ).ifPresent(entity::setStaffEntity);
            }
        }

        // ================================
        // 5. Gán ngày thuê
        // ================================
        entity.setHireDate(Date.valueOf(LocalDate.now()));

        // ================================
        // 6. Lưu vào bảng Staff
        // ================================
        StaffEntity saved = staffRepo.save(entity);

        // ================================
        // 7. Nếu là bác sĩ thì lưu vào bảng Doctor
        // ================================
        if (request.getStaffRole() != null && "DOCTOR".equalsIgnoreCase(request.getStaffRole())) {
            DoctorEntity doctor = new DoctorEntity();
            doctor.setStaffEntity(saved);

            if (request.getDoctorRank() != null && !request.getDoctorRank().isBlank()) {
                try {
                    doctor.setDoctorRank(DoctorRank.valueOf(request.getDoctorRank().toUpperCase()));
                } catch (IllegalArgumentException ignored) {
                    // Không gán nếu sai enum
                }
            }

            doctorRepo.save(doctor);
        }

        // ================================
        // 8. Trả về response
        // ================================
        return mapper.toResponse(saved);
    }

    @Override
    public DoctorStaffResponse getDoctorStaff(Long staffId) {
        return staffRepo.findById(staffId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Nhân viên không tồn tại"));
    }

    @Override
    public DoctorStaffRequest getUpdateForm(Long staffId) {
        StaffEntity entity = staffRepo.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException("Nhân viên không tồn tại"));

        DoctorStaffRequest dto = new DoctorStaffRequest();
        if (entity.getUserEntity() != null) {
            dto.setUserId(entity.getUserEntity().getId());
            dto.setEmail(entity.getUserEntity().getEmail());
            dto.setPhoneNumber(entity.getUserEntity().getPhoneNumber());
        }
        dto.setFullName(entity.getFullName());
        if (entity.getStaffRole() != null) {
            dto.setStaffRole(entity.getStaffRole().name());
        }
        if (entity.getStaffType() != null) {
            dto.setStaffType(entity.getStaffType().name());
        }
        dto.setRankLevel(entity.getRankLevel());
        dto.setAvatarUrl(entity.getAvatarUrl());
        if (entity.getDepartmentEntity() != null) {
            dto.setDepartmentId(entity.getDepartmentEntity().getId());
        }
        if (entity.getHospitalEntity() != null) {
            dto.setHospitalId(entity.getHospitalEntity().getId());
        }
        if (entity.getStaffEntity() != null) {
            dto.setManagerId(entity.getStaffEntity().getId());
        }
        if (entity.getDoctorEntity() != null && entity.getDoctorEntity().getDoctorRank() != null) {
            dto.setDoctorRank(entity.getDoctorEntity().getDoctorRank().name());
        }
        return dto;
    }

    @Override
    public List<DoctorStaffResponse> getAllDoctorStaff() {
        return staffRepo.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DoctorStaffResponse> searchDoctorStaff(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllDoctorStaff();
        }
        return staffRepo
                .findByFullNameContainingIgnoreCaseOrUserEntityEmailContainingIgnoreCaseOrUserEntityPhoneNumberContainingIgnoreCase(
                        keyword, keyword, keyword, Pageable.unpaged())
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DoctorStaffResponse> searchDoctorStaff(String field, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllDoctorStaff();
        }
        return staffRepo.searchStaffs(Pageable.unpaged(), field, keyword)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

}
