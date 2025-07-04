package org.project.mapper;

import lombok.RequiredArgsConstructor;
import org.project.entity.*;
import org.project.enums.StaffRole;
import org.project.enums.StaffType;
import org.project.model.request.DoctorStaffRequest;
import org.project.model.response.DoctorStaffResponse;
import org.project.repository.*;
import org.springframework.stereotype.Component;

import java.sql.Date;

/**
 * Manual implementation of {@link DoctorStaffMapper} to avoid MapStruct dependency.
 * This component is registered in the Spring context so it can be autowired wherever needed.
 */
@Component
@RequiredArgsConstructor
public class DoctorStaffMapperImpl implements DoctorStaffMapper {

    private final UserRepository userRepo;
    private final DepartmentRepository deptRepo;
    private final HospitalRepository hospitalRepo;
    private final StaffRepository staffRepo;

    @Override
    public DoctorStaffResponse toResponse(StaffEntity entity) {
        if (entity == null) {
            return null;
        }
        DoctorStaffResponse res = new DoctorStaffResponse();
        res.setStaffId(entity.getId());
        res.setFullName(entity.getFullName());
        res.setRankLevel(entity.getRankLevel());
        res.setAvatarUrl(entity.getAvatarUrl());
        res.setHireDate(entity.getHireDate() != null ? entity.getHireDate().toString() : null);

        if (entity.getStaffRole() != null) {
            res.setStaffRole(entity.getStaffRole().name());
        }
        if (entity.getStaffType() != null) {
            res.setStaffType(entity.getStaffType().name());
        }

        // User info
        if (entity.getUserEntity() != null) {
            UserEntity user = entity.getUserEntity();
            res.setUserId(user.getId());
            res.setEmail(user.getEmail());
            res.setPhoneNumber(user.getPhoneNumber());
        }

        // Department
        if (entity.getDepartmentEntity() != null) {
            DepartmentEntity dept = entity.getDepartmentEntity();
            res.setDepartmentId(dept.getId());
            res.setDepartmentName(dept.getName());
        }

        // Hospital
        if (entity.getHospitalEntity() != null) {
            HospitalEntity hospital = entity.getHospitalEntity();
            res.setHospitalId(hospital.getId());
            res.setHospitalName(hospital.getName());
        }

        // Manager
        if (entity.getStaffEntity() != null) {
            StaffEntity manager = entity.getStaffEntity();
            res.setManagerId(manager.getId());
            res.setManagerName(manager.getFullName());
        }

        // Doctor specific (if applicable)
        if (entity.getDoctorEntity() != null) {
            res.setDoctorRank(entity.getDoctorEntity().getDoctorRank() != null ? entity.getDoctorEntity().getDoctorRank().name() : null);
        }
        return res;
    }

    @Override
    public StaffEntity toEntity(DoctorStaffRequest request) {
        if (request == null) {
            return null;
        }
        StaffEntity entity = new StaffEntity();

        // Simple scalar fields
        entity.setFullName(request.getFullName());
        entity.setRankLevel(request.getRankLevel() != null ? request.getRankLevel() : 1);
        entity.setAvatarUrl(request.getAvatarUrl());

        // Enums
        if (request.getStaffRole() != null) {
            try {
                entity.setStaffRole(StaffRole.valueOf(request.getStaffRole().toUpperCase()));
            } catch (IllegalArgumentException ignored) { }
        }
        if (request.getStaffType() != null) {
            try {
                entity.setStaffType(StaffType.valueOf(request.getStaffType().toUpperCase()));
            } catch (IllegalArgumentException ignored) { }
        }

        // Relationships + phone handling
        if (request.getUserId() != null) {
            UserEntity user = userRepo.findById(request.getUserId()).orElse(null);
            if (user != null) {
                if (request.getPhoneNumber() != null) {
                    user.setPhoneNumber(request.getPhoneNumber());
                }
                if (request.getEmail() != null) {
                    user.setEmail(request.getEmail());
                }
            }
            entity.setUserEntity(user);
        }
        if (request.getDepartmentId() != null) {
            DepartmentEntity dept = deptRepo.findById(request.getDepartmentId()).orElse(null);
            entity.setDepartmentEntity(dept);
        }
        if (request.getHospitalId() != null) {
            HospitalEntity hospital = hospitalRepo.findById(request.getHospitalId()).orElse(null);
            entity.setHospitalEntity(hospital);
        }
        if (request.getManagerId() != null) {
            StaffEntity manager = staffRepo.findById(request.getManagerId()).orElse(null);
            entity.setStaffEntity(manager);
        }

        // hireDate left for service layer to set
        entity.setHireDate((Date) null);

        return entity;
    }
}
