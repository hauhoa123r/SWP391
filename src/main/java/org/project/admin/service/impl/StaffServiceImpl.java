package org.project.admin.service.impl;

import org.project.admin.dto.request.DoctorRequest;
import org.project.admin.dto.request.StaffRequest;
import org.project.admin.dto.request.StaffSearchRequest;
import org.project.admin.dto.response.SchedulingCoordinatorResponse;
import org.project.admin.dto.response.StaffResponse;
import org.project.admin.entity.*;
import org.project.admin.enums.AuditAction;
import org.project.admin.enums.staffs.StaffRole;
import org.project.admin.enums.users.UserRole;
import org.project.admin.mapper.DoctorMapper;
import org.project.admin.mapper.StaffMapper;
import org.project.admin.repository.*;
import org.project.admin.service.DoctorService;
import org.project.admin.service.Log.StaffLogService;
import org.project.admin.service.StaffService;
import org.project.admin.specification.StaffSpecification;
import org.project.admin.util.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service("adminStaffService")
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final StaffMapper staffMapper;
    private final DepartmentRepository departmentRepository;
    private final HospitalRepository hospitalRepository;
    private final UserRepository userRepository;
    private final StaffLogService staffLogService;
    private final DoctorMapper doctorMapper;
    private final DoctorService doctorService;
    private final DoctorRepository doctorRepository;

    @Override
    public StaffResponse createStaff(StaffRequest request) {
        // Validation 1: Kiểm tra User tồn tại và có role STAFF
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User ID không tồn tại trong hệ thống"));

        if (!UserRole.STAFF.equals(user.getUserRole())) {
            throw new IllegalArgumentException("Chỉ tài khoản có vai trò STAFF mới được tạo hồ sơ nhân viên");
        }

        // Validation 2: Kiểm tra StaffRole thuộc danh sách cho phép
        List<StaffRole> allowedRoles = List.of(
            StaffRole.DOCTOR,
            StaffRole.TECHNICIAN,
            StaffRole.SCHEDULING_COORDINATOR,
            StaffRole.PHARMACIST,
            StaffRole.INVENTORY_MANAGER
        );

        if (!allowedRoles.contains(request.getStaffRole())) {
            throw new IllegalArgumentException("Vai trò nhân viên phải là một trong: DOCTOR, TECHNICIAN, SCHEDULING_COORDINATOR, PHARMACIST, INVENTORY_MANAGER");
        }

        // Validation 3: Kiểm tra Department tồn tại
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("Phòng ban không tồn tại trong hệ thống"));

        // Validation 4: Kiểm tra Hospital tồn tại
        Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                .orElseThrow(() -> new IllegalArgumentException("Bệnh viện không tồn tại trong hệ thống"));

        // Validation 5: Kiểm tra Manager nếu có
        Staff manager = null;
        if (request.getManagerId() != null) {
            manager = staffRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new IllegalArgumentException("Quản lý không tồn tại trong hệ thống"));
        }

        // Tạo Staff entity
        Staff staff = staffMapper.toEntity(request);
        staff.setDepartment(department);
        staff.setHospital(hospital);
        staff.setManager(manager);
        staff.setUser(user);

        staff = staffRepository.save(staff);

        // Tạo Doctor record nếu là DOCTOR role
        if (request.getStaffRole() == StaffRole.DOCTOR) {
            DoctorRequest doctorRequest = new DoctorRequest();
            doctorRequest.setStaffId(staff.getStaffId());
            doctorRequest.setRankLevel(request.getRankLevel());
            doctorService.createDoctor(doctorRequest);
        }

        staffLogService.logStaffAction(staff, AuditAction.CREATE);
        return staffMapper.toResponse(staff);
    }


    @Override
    public PageResponse<StaffResponse> getAllStaffPaged(int page, int size) {
        Page<Staff> staffPage = staffRepository.findAll(PageRequest.of(page, size));
        Page<StaffResponse> mappedPage = staffPage.map(staffMapper::toResponse);
        return new PageResponse<>(mappedPage);
    }

    @Override
    public StaffResponse getStaffById(Long id) {
        return staffRepository.findById(id)
                .map(staffMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));
    }

    @Override
    public PageResponse<StaffResponse> search(StaffSearchRequest request, int page, int size) {
        Specification<Staff> spec = StaffSpecification.filter(request);
        Page<Staff> staffPage = staffRepository.findAll(spec, PageRequest.of(page, size));
        Page<StaffResponse> mappedPage = staffPage.map(staffMapper::toResponse);
        return new PageResponse<>(mappedPage);
    }

    @Override
    public StaffResponse updateStaff(Long id, StaffRequest request) {
        // Lấy staff hiện tại
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên"));
        StaffResponse oldStaff = staffMapper.toResponse(staff);

        // Validation 1: Kiểm tra User tồn tại và có role STAFF
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User ID không tồn tại trong hệ thống"));

        if (!UserRole.STAFF.equals(user.getUserRole())) {
            throw new IllegalArgumentException("Chỉ tài khoản có vai trò STAFF mới được tạo hồ sơ nhân viên");
        }

        // Validation 2: Kiểm tra StaffRole thuộc danh sách cho phép
        List<StaffRole> allowedRoles = List.of(
            StaffRole.DOCTOR,
            StaffRole.TECHNICIAN,
            StaffRole.SCHEDULING_COORDINATOR,
            StaffRole.PHARMACIST,
            StaffRole.INVENTORY_MANAGER
        );

        if (!allowedRoles.contains(request.getStaffRole())) {
            throw new IllegalArgumentException("Vai trò nhân viên phải là một trong: DOCTOR, TECHNICIAN, SCHEDULING_COORDINATOR, PHARMACIST, INVENTORY_MANAGER");
        }

        // Validation 3: Kiểm tra Department tồn tại
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("Phòng ban không tồn tại trong hệ thống"));

        // Validation 4: Kiểm tra Hospital tồn tại
        Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                .orElseThrow(() -> new IllegalArgumentException("Bệnh viện không tồn tại trong hệ thống"));

        // Validation 5: Kiểm tra Manager nếu có
        Staff manager = null;
        if (request.getManagerId() != null) {
            manager = staffRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new IllegalArgumentException("Quản lý không tồn tại trong hệ thống"));
        }

        // Cập nhật thông tin staff
        staff.setDepartment(department);
        staff.setHospital(hospital);
        staff.setManager(manager);
        staff.setUser(user);
        staff.setFullName(request.getFullName());
        staff.setAvatarUrl(request.getAvatarUrl());
        staff.setHireDate(request.getHireDate());
        staff.setRankLevel(request.getRankLevel());
        staff.setStaffRole(request.getStaffRole());
        staff.setStaffType(request.getStaffType());

        staff = staffRepository.save(staff);

        // Xử lý Doctor record
        if (request.getStaffRole() == StaffRole.DOCTOR) {
            Doctor doctor = doctorRepository.findById(staff.getStaffId()).orElse(null);
            if (doctor == null) {
                doctor = new Doctor();
                doctor.setDoctorId(staff.getStaffId());
                doctor.setStaff(staff);
            }
            doctor.setDoctorRank(doctorMapper.mapRankLevelToEnum(request.getRankLevel()));
            doctorRepository.save(doctor);
        } else {
            // Nếu không còn là DOCTOR, xóa record Doctor
            doctorRepository.deleteById(staff.getStaffId());
        }

        StaffResponse newStaff = staffMapper.toResponse(staff);
        staffLogService.logStaffUpdateAction(oldStaff, newStaff, AuditAction.UPDATE);
        return newStaff;
    }



    @Override
    public void deleteStaff(Long id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));
        staffLogService.logStaffAction(staff, AuditAction.DELETE);
        staffRepository.delete(staff);
    }


    public List<SchedulingCoordinatorResponse> getSchedulingCoordinators(String name) {
        List<Staff> list;
        if (name != null && !name.isEmpty()) {
            list = staffRepository.findByStaffRoleAndFullNameContainingIgnoreCase(StaffRole.SCHEDULING_COORDINATOR, name);
        } else {
            list = staffRepository.findByStaffRole(StaffRole.SCHEDULING_COORDINATOR);
        }
        return list.stream().map(staff -> {
            SchedulingCoordinatorResponse resp = new SchedulingCoordinatorResponse();
            resp.setStaffId(staff.getStaffId());
            resp.setFullName(staff.getFullName());
            resp.setAvatarUrl(staff.getAvatarUrl());
            return resp;
        }).collect(Collectors.toList());
    }

    @Override
    public List<StaffResponse> searchStaffsByName(String name) {
        List<Staff> staffs = staffRepository.findByFullNameContainingIgnoreCase(name);
        return staffMapper.toResponse(staffs);
    }


}
