package org.project.utils;

import org.project.enums.*;
import org.project.exception.BadRequestException;
import org.project.repository.DepartmentRepository;
import org.project.repository.HospitalRepository;
import org.project.repository.StaffRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ValidationUtils {

    public void validateEmail(String email) {
        if (email == null || !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new BadRequestException("Email không hợp lệ");
        }
    }

    public void validatePhoneNumber(String phone) {
        if (phone == null || !phone.matches("0\\d{9}")) {
            throw new BadRequestException("Số điện thoại phải bắt đầu bằng 0 và gồm 10 chữ số");
        }
    }

    public void validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new BadRequestException("Mật khẩu phải có ít nhất 6 ký tự");
        }
    }

    public void validateStaffRole(StaffRole role) {
        if (role == null) {
            throw new BadRequestException("StaffRole không hợp lệ");
        }
    }

    public void validateStaffType(StaffType type) {
        if (type == null) {
            throw new BadRequestException("StaffType không hợp lệ");
        }
    }

    public void validateRankLevel(Integer rankLevel) {
        if (rankLevel == null || rankLevel < 1 || rankLevel > 7) {
            throw new BadRequestException("Rank level phải từ 1 đến 7");
        }
    }

    public void validateHireDate(LocalDate date) {
        if (date == null || !date.isBefore(LocalDate.now())) {
            throw new BadRequestException("Ngày vào làm phải trong quá khứ");
        }
    }

    public void validateDepartmentId(Long departmentId, DepartmentRepository departmentRepository) {
        if (departmentId == null || !departmentRepository.existsById(departmentId)) {
            throw new BadRequestException("Phòng ban không tồn tại");
        }
    }

    public void validateHospitalId(Long hospitalId, HospitalRepository hospitalRepository) {
        if (hospitalId == null || !hospitalRepository.existsById(hospitalId)) {
            throw new BadRequestException("Bệnh viện không tồn tại");
        }
    }

    public void validateManagerId(Long managerId, DepartmentRepository departmentRepository,
                                  StaffRepository staffRepository, Long departmentId) {
        if (managerId == null || !staffRepository.existsById(managerId)) {
            throw new BadRequestException("Người quản lý không tồn tại");
        }

        staffRepository.findById(managerId).ifPresent(manager -> {
            if (!manager.getDepartmentEntity().getId().equals(departmentId)) {
                throw new BadRequestException("Người quản lý không thuộc cùng phòng ban");
            }
        });
    }

    public void validateUserStatus(UserStatus userStatus) {
        if (userStatus == null) {
            throw new BadRequestException("Trạng thái người dùng không hợp lệ");
        }
    }
}
