package org.project.service.impl;

import org.project.config.WebConstant;
import org.project.converter.StaffConverter;
import org.project.converter.StaffScheduleConverter;
import org.project.converter.UserConverter;
import org.project.entity.StaffEntity;
import org.project.entity.UserEntity;
import org.project.enums.operation.ComparisonOperator;
import org.project.exception.ErrorResponse;
import org.project.model.dto.AvailabilityRequestDTO;
import org.project.model.dto.StaffDTO;
import org.project.model.response.StaffResponse;
import org.project.model.response.StaffSubstituteResponse;
import org.project.repository.DepartmentRepository;
import org.project.repository.HospitalRepository;
import org.project.repository.StaffRepository;
import org.project.repository.UserRepository;
import org.project.service.EmailService;
import org.project.service.StaffService;
import org.project.service.UserService;
import org.project.utils.MergeObjectUtils;
import org.project.utils.PageUtils;
import org.project.utils.RandomUtils;
import org.project.utils.specification.SpecificationUtils;
import org.project.utils.specification.search.SearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.Objects;

@Service
public class StaffServiceImpl implements StaffService {

    private EmailService emailService;
    private StaffRepository staffRepository;
    private DepartmentRepository departmentRepository;
    private HospitalRepository hospitalRepository;
    private StaffScheduleConverter staffScheduleConverter;
    private StaffConverter staffConverter;
    private PageUtils<StaffEntity> pageUtils;
    private SpecificationUtils<StaffEntity> specificationUtils;
    private UserService userService;
    private UserRepository userRepository;
    private UserConverter userConverter;

    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Autowired
    public void setDepartmentRepository(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Autowired
    public void setHospitalRepository(HospitalRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
    }

    @Autowired
    public void setStaffRepository(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setStaffScheduleConverter(StaffScheduleConverter staffScheduleConverter) {
        this.staffScheduleConverter = staffScheduleConverter;
    }

    @Autowired
    public void setStaffConverter(StaffConverter staffConverter) {
        this.staffConverter = staffConverter;
    }

    @Autowired
    public void setPageUtils(PageUtils<StaffEntity> pageUtils) {
        this.pageUtils = pageUtils;
    }

    @Autowired
    public void setSpecificationUtils(SpecificationUtils<StaffEntity> specificationUtils) {
        this.specificationUtils = specificationUtils;
    }

    @Autowired
    public void setUserConverter(UserConverter userConverter) {
        this.userConverter = userConverter;
    }

    @Override
    public List<StaffSubstituteResponse> getAllStaffSubstitutes(AvailabilityRequestDTO availability) {
        if (isStaffExist(availability.getStaffId())) {
            StaffEntity staffEntity = staffRepository.findById(availability.getStaffId()).get();
            List<StaffEntity> staffSubstitutes = staffRepository.findAvailableSubstitutesNative(
                    staffEntity.getDepartmentEntity().getId(),
                    staffEntity.getHospitalEntity().getId(),
                    availability.getStaffId(),
                    staffEntity.getStaffRole().toString(),
                    availability.getStartTime(),
                    availability.getEndTime()
            );
            return staffSubstitutes.stream()
                    .map(staffScheduleConverter::toResponseSubstitute)
                    .toList();
        }
        return List.of();
    }

    @Override
    public boolean isStaffExist(Long staffId) {
        return staffRepository.existsByIdAndStaffStatus(staffId, WebConstant.STAFF_STATUS_ACTIVE);
    }

    @Override
    public StaffEntity getManagerByStaffId(Long staffId) {
        StaffEntity staffEntity = getStaffByStaffId(staffId);
        if (staffEntity.getManager() == null) {
            throw new IllegalArgumentException("Staff with ID " + staffId + " does not have a manager.");
        }
        return staffEntity.getManager();
    }

    @Override
    public StaffEntity getStaffByStaffId(Long staffId) {
        StaffEntity staffEntity = staffRepository.findByIdAndStaffStatus(staffId, WebConstant.STAFF_STATUS_ACTIVE);
        if (staffEntity == null) {
            throw new ErrorResponse("Nhân viên không tồn tại hoặc đã bị xóa");
        }
        return staffEntity;
    }

    @Override
    public void deleteStaff(Long staffId) {
        StaffEntity staffEntity = getStaffByStaffId(staffId);
        if (staffEntity.getManager() == null) {
            throw new ErrorResponse("Không thể xóa nhân viên quản lý, vui lòng chuyển giao quyền quản lý trước khi xóa.");
        }

        UserEntity userEntity = staffEntity.getUserEntity();
        if (userEntity != null) {
            userEntity.setUserStatus(WebConstant.USER_STATUS_INACTIVE);
            userRepository.save(userEntity);
        }

        staffEntity.setStaffStatus(WebConstant.STAFF_STATUS_INACTIVE);
        staffRepository.save(staffEntity);
    }

    @Override
    public Page<StaffResponse> getStaffs(int index, int size, StaffDTO staffDTO) {
        Sort sort = Sort.unsorted();
        if (staffDTO.getSortFieldName() != null && !staffDTO.getSortFieldName().isEmpty()) {
            sort = Sort.by(Sort.Direction.fromString(staffDTO.getSortDirection()), staffDTO.getSortFieldName());
        }
        Pageable pageable = pageUtils.getPageable(index, size, sort);
        Page<StaffEntity> staffEntityPage = staffRepository.findAll(
                specificationUtils.reset()
                        .getSearchSpecifications(
                                new SearchCriteria("fullName", ComparisonOperator.CONTAINS, staffDTO.getFullName()),
                                new SearchCriteria("userEntity.email", ComparisonOperator.EQUALS, staffDTO.getEmail()),
                                new SearchCriteria("userEntity.phoneNumber", ComparisonOperator.EQUALS, staffDTO.getPhoneNumber()),
                                new SearchCriteria("departmentEntity.id", ComparisonOperator.EQUALS, staffDTO.getDepartmentEntityId()),
                                new SearchCriteria("hospitalEntity.id", ComparisonOperator.EQUALS, staffDTO.getHospitalEntityId()),
                                new SearchCriteria("manager.fullName", ComparisonOperator.CONTAINS, staffDTO.getManagerFullName()),
                                new SearchCriteria("staffRole", ComparisonOperator.EQUALS, staffDTO.getStaffRole()),
                                new SearchCriteria("staffStatus", ComparisonOperator.EQUALS, WebConstant.STAFF_STATUS_ACTIVE)
                        ), pageable);
        pageUtils.validatePage(staffEntityPage, StaffEntity.class);
        return staffEntityPage.map(staffConverter::toResponse);
    }

    @Override
    public void createStaff(StaffDTO staffDTO) {
        // validate and convert DTO to entity
        // Check if department and hospital exist
        if (!departmentRepository.existsByIdAndDepartmentStatus(staffDTO.getDepartmentEntityId(), WebConstant.DEPARTMENT_STATUS_ACTIVE)) {
            throw new ErrorResponse("Phòng ban không tồn tại hoặc đã bị xóa");
        }

        if (!hospitalRepository.existsByIdAndHospitalStatus(staffDTO.getHospitalEntityId(), WebConstant.HOSPITAL_STATUS_ACTIVE)) {
            throw new ErrorResponse("Bệnh viện không tồn tại hoặc đã bị xóa");
        }

        // Check if duplicate email or phone number exists
        if (staffRepository.existsByUserEntityEmailAndStaffStatus(
                staffDTO.getEmail(),
                WebConstant.STAFF_STATUS_ACTIVE)) {
            throw new ErrorResponse("Email đã tồn tại");
        }

        if (staffRepository.existsByUserEntityPhoneNumberAndStaffStatus(
                staffDTO.getPhoneNumber(),
                WebConstant.STAFF_STATUS_ACTIVE)) {
            throw new ErrorResponse("Số điện thoại đã tồn tại");
        }

        StaffEntity staffEntity = staffConverter.toEntity(staffDTO);
        staffEntity.setStaffStatus(WebConstant.STAFF_STATUS_ACTIVE);
        staffEntity.setHireDate(new Date(System.currentTimeMillis()));
        staffEntity.setRankLevel(1);
        staffEntity.setManager(staffRepository.findByHospitalEntityIdAndDepartmentEntityIdAndManager(staffEntity.getHospitalEntity().getId(), staffEntity.getDepartmentEntity().getId(), null));

        UserEntity userEntity = userConverter.toEntity(staffDTO);
        String password = RandomUtils.generateStringFromEnableCharacter(WebConstant.ENABLE_CHARACTERS_PASSWORD, 6);
        userEntity.setPasswordHash(password);
        userEntity = userService.createUser(userEntity);
        emailService.sendAccountCreatedEmail(userEntity.getEmail(), password);

        staffEntity.setUserEntity(userEntity);
        staffEntity.setHireDate(new Date(System.currentTimeMillis()));
        staffRepository.save(staffEntity);
    }

    private boolean isStaffChangeHospitalOrDepartment(StaffEntity target, StaffEntity source) {
        return !Objects.equals(target.getDepartmentEntity().getId(), source.getDepartmentEntity().getId()) ||
                !Objects.equals(target.getHospitalEntity().getId(), source.getHospitalEntity().getId());
    }

    @Override
    public void updateStaff(Long staffId, StaffDTO staffDTO) {
        staffDTO.setId(null);

        StaffEntity target = staffRepository.findByIdAndStaffStatus(staffId, WebConstant.STAFF_STATUS_ACTIVE);
        if (target == null) {
            throw new Error("Nhân viên không tồn tại hoặc đã bị xóa");
        }

        // Check if department and hospital exist
        if (!departmentRepository.existsByIdAndDepartmentStatus(staffDTO.getDepartmentEntityId(), WebConstant.DEPARTMENT_STATUS_ACTIVE)) {
            throw new ErrorResponse("Phòng ban không tồn tại hoặc đã bị xóa");
        }

        if (!hospitalRepository.existsByIdAndHospitalStatus(staffDTO.getHospitalEntityId(), WebConstant.HOSPITAL_STATUS_ACTIVE)) {
            throw new ErrorResponse("Bệnh viện không tồn tại hoặc đã bị xóa");
        }

        // Check if duplicate email or phone number exists
        if (staffRepository.existsByUserEntityEmailAndIdNotAndStaffStatus(
                staffDTO.getEmail(),
                staffId,
                WebConstant.STAFF_STATUS_ACTIVE)) {
            throw new ErrorResponse("Email đã tồn tại");
        }

        if (staffRepository.existsByUserEntityPhoneNumberAndIdNotAndStaffStatus(
                staffDTO.getPhoneNumber(),
                staffId,
                WebConstant.STAFF_STATUS_ACTIVE)) {
            throw new ErrorResponse("Số điện thoại đã tồn tại");
        }

        // Update staff entity
        StaffEntity source = staffConverter.toEntity(staffDTO);

        // Check if staff is manager and change department or hospital
        if (target.getManager() == null && isStaffChangeHospitalOrDepartment(target, source)) {
            throw new ErrorResponse("Không thể thay đổi phòng ban hoặc bệnh viện của nhân viên quản lý");
        }

        // If staff change manager or department assign new manager for staff
        if (isStaffChangeHospitalOrDepartment(target, source)) {
            StaffEntity manager = staffRepository.findByHospitalEntityIdAndDepartmentEntityIdAndManager(
                    source.getHospitalEntity().getId(),
                    source.getDepartmentEntity().getId(),
                    null);
            target.setManager(manager);
        }

        MergeObjectUtils.mergeNonNullFields(source, target);
        staffRepository.save(target);
    }

    @Override
    public void setStaffToManager(Long staffId) {
        StaffEntity staffEntity = getStaffByStaffId(staffId);
        StaffEntity manager = staffRepository.findByHospitalEntityIdAndDepartmentEntityIdAndManager(
                staffEntity.getHospitalEntity().getId(),
                staffEntity.getDepartmentEntity().getId(),
                null
        );
        if (manager != null && manager.getId().equals(staffEntity.getId())) {
            throw new ErrorResponse("Nhân viên đã là quản lý, không thể nâng cấp.");
        }

        staffRepository.upgradeStaffToManager(staffEntity, staffEntity.getHospitalEntity().getId(), staffEntity.getDepartmentEntity().getId());
        staffEntity.setManager(null);

        staffRepository.save(staffEntity);
    }
}
