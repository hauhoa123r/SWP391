package org.project.service.impl;

import lombok.RequiredArgsConstructor;
import org.project.entity.DepartmentEntity;
import org.project.entity.HospitalEntity;
import org.project.entity.StaffEntity;
import org.project.entity.DoctorEntity;
import org.project.enums.DoctorRank;
import org.project.enums.StaffRole;
import org.project.entity.UserEntity;
import org.project.mapper.DoctorStaffMapper;
import org.project.model.request.DoctorStaffRequest;
import org.project.model.response.DoctorStaffResponse;
import org.project.repository.DepartmentRepository;
import org.project.repository.HospitalRepository;
import org.project.repository.StaffRepository;
import org.project.repository.DoctorRepository;
import org.project.repository.UserRepository;
import org.project.service.DoctorStaffService;
import org.project.validation.DoctorStaffValidator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

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
        validator.validate(request);
        StaffEntity entity = mapper.toEntity(request);

        // Tạo User nếu chưa có
        if (request.getUserId() == null) {
            UserEntity user = new UserEntity();
            user.setEmail(request.getEmail());
            user.setPhoneNumber(request.getPhoneNumber());
            userRepo.save(user);
            entity.setUserEntity(user);
        } else {
            entity.setUserEntity(userRepo.findById(request.getUserId()).orElse(null));
        }

        // Phòng ban
        DepartmentEntity dept = (request.getDepartmentId() != null)
                ? deptRepo.findById(request.getDepartmentId()).orElse(null)
                : deptRepo.findAll().stream().findFirst().orElse(null);
        entity.setDepartmentEntity(dept);

        // Bệnh viện
        if (request.getHospitalId() != null) {
            HospitalEntity hospital = hospitalRepo.findById(request.getHospitalId()).orElse(null);
            entity.setHospitalEntity(hospital);

            // Gán quản lý
            if (dept != null && entity.getStaffEntity() == null) {
                staffRepo.findFirstByDepartmentEntity_IdAndHospitalEntity_IdOrderByRankLevelAsc(
                        dept.getId(), hospital.getId()
                ).ifPresent(entity::setStaffEntity);
            }
        }

        entity.setHireDate(Date.valueOf(LocalDate.now()));

        StaffEntity saved = staffRepo.save(entity);

        if (request.getStaffRole() != null && "DOCTOR".equalsIgnoreCase(request.getStaffRole())) {
            DoctorEntity doctor = new DoctorEntity();
            doctor.setStaffEntity(saved);
            // Map doctor rank if provided
            if (request.getDoctorRank() != null && !request.getDoctorRank().isBlank()) {
                try {
                    doctor.setDoctorRank(DoctorRank.valueOf(request.getDoctorRank().toUpperCase()));
                } catch (IllegalArgumentException ignored) {}
            }
            doctorRepo.save(doctor);
        }

        return mapper.toResponse(saved);
    }

    @Override
    public List<DoctorStaffResponse> getAllDoctorStaff() {
        return staffRepo.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
}
