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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
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

    @Value("${app.upload.dir:uploads/avatars}")
    private String uploadDir;

    // ================================
    // 1. Tạo nhân viên
    // ================================
    @Override
    public DoctorStaffResponse createDoctorStaff(DoctorStaffRequest request) {
        validator.validate(request);
        StaffEntity entity = mapper.toEntity(request);

        // 1.1 Tạo user nếu chưa có
        if (request.getUserId() == null) {
            UserEntity user = new UserEntity();
            user.setEmail(request.getEmail());
            user.setPhoneNumber(request.getPhoneNumber());
            userRepo.save(user);
            entity.setUserEntity(user);
        } else {
            entity.setUserEntity(userRepo.findById(request.getUserId()).orElse(null));
        }

        // 1.2 Gán phòng ban nếu có
        DepartmentEntity dept = (request.getDepartmentId() != null)
                ? deptRepo.findById(request.getDepartmentId()).orElse(null)
                : null;
        entity.setDepartmentEntity(dept);

        // 1.3 Gán bệnh viện và quản lý tự động
        if (request.getHospitalId() != null) {
            HospitalEntity hospital = hospitalRepo.findById(request.getHospitalId()).orElse(null);
            entity.setHospitalEntity(hospital);

            if (dept != null && entity.getStaffEntity() == null) {
                staffRepo.findFirstByDepartmentEntity_IdAndHospitalEntity_IdOrderByRankLevelAsc(
                        dept.getId(), hospital.getId()
                ).ifPresent(entity::setStaffEntity);
            }
        }

        // 1.4 Gán ngày thuê
        entity.setHireDate(Date.valueOf(LocalDate.now()));

        // 1.5 Lưu staff
        StaffEntity saved = staffRepo.save(entity);

        // 1.6 Nếu là bác sĩ thì lưu vào bảng Doctor
        if ("DOCTOR".equalsIgnoreCase(request.getStaffRole())) {
            DoctorEntity doctor = new DoctorEntity();
            doctor.setStaffEntity(saved);
            if (request.getDoctorRank() != null && !request.getDoctorRank().isBlank()) {
                try {
                    doctor.setDoctorRank(DoctorRank.valueOf(request.getDoctorRank().toUpperCase()));
                } catch (IllegalArgumentException ignored) {
                }
            }
            doctorRepo.save(doctor);
        }

        return mapper.toResponse(saved);
    }

    // ================================
    // 2. Upload Avatar
    // ================================
    @Override
    public String handleAvatarUpload(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;

        String contentType = file.getContentType();
        if (contentType == null || (!contentType.startsWith("image/jpeg")
                && !contentType.startsWith("image/png")
                && !contentType.startsWith("image/jpg"))) {
            throw new IllegalArgumentException("Chỉ chấp nhận file ảnh định dạng JPG, JPEG, PNG");
        }

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath);

        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/" + uploadDir + "/")
                .path(uniqueFilename)
                .toUriString();
    }

    // ================================
    // 3. Lấy chi tiết nhân viên
    // ================================
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
        if (entity.getStaffRole() != null) dto.setStaffRole(entity.getStaffRole().name());
        if (entity.getStaffType() != null) dto.setStaffType(entity.getStaffType().name());
        dto.setRankLevel(entity.getRankLevel());
        dto.setAvatarUrl(entity.getAvatarUrl());
        if (entity.getDepartmentEntity() != null) dto.setDepartmentId(entity.getDepartmentEntity().getId());
        if (entity.getHospitalEntity() != null) dto.setHospitalId(entity.getHospitalEntity().getId());
        if (entity.getStaffEntity() != null) dto.setManagerId(entity.getStaffEntity().getId());
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
        if (keyword == null || keyword.isBlank()) return getAllDoctorStaff();
        return staffRepo.findByFullNameContainingIgnoreCaseOrUserEntityEmailContainingIgnoreCaseOrUserEntityPhoneNumberContainingIgnoreCase(
                        keyword, keyword, keyword, Pageable.unpaged())
                .stream().map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DoctorStaffResponse> searchDoctorStaff(String field, String keyword) {
        if (keyword == null || keyword.isBlank()) return getAllDoctorStaff();
        return staffRepo.searchStaffs(Pageable.unpaged(), field, keyword)
                .stream().map(mapper::toResponse)
                .collect(Collectors.toList());
    }
}
