package org.project.validation;

import lombok.RequiredArgsConstructor;
import org.project.model.request.DoctorStaffRequest;
import org.project.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class DoctorStaffValidator {

    private final UserRepository userRepository;

    public void validate(DoctorStaffRequest request) {
        // Email
        if (!StringUtils.hasText(request.getEmail())) {
            throw new IllegalArgumentException("Email không được để trống");
        }
        if (!request.getEmail().matches("^[A-Za-z0-9]+(?:\\.[A-Za-z0-9]+)*@gmail\\.com$")) {
            throw new IllegalArgumentException("Email phải thuộc miền gmail.com và không chứa ký tự đặc biệt");
        }

        // Check email trùng
        boolean exists = userRepository.existsByEmail(request.getEmail());
        if (exists && request.getUserId() == null) {
            throw new IllegalArgumentException("Email đã tồn tại trong hệ thống");
        }

        // Phone
        if (!StringUtils.hasText(request.getPhoneNumber())) {
            throw new IllegalArgumentException("Số điện thoại không được để trống");
        }
        if (!request.getPhoneNumber().matches("^[0-9]{10}$")) {
            throw new IllegalArgumentException("Số điện thoại phải gồm đúng 10 chữ số");
        }

        // Tên
        if (!StringUtils.hasText(request.getFullName())) {
            throw new IllegalArgumentException("Vui lòng nhập họ tên");
        }

        // Vai trò
        if (!StringUtils.hasText(request.getStaffRole())) {
            throw new IllegalArgumentException("Vui lòng chọn vai trò nhân viên");
        }

        // Chức danh bác sĩ
        if ("DOCTOR".equalsIgnoreCase(request.getStaffRole()) &&
                !StringUtils.hasText(request.getDoctorRank())) {
            throw new IllegalArgumentException("Vui lòng nhập học hàm/chức danh nếu vai trò là bác sĩ");
        }

        // Bệnh viện
        if (request.getHospitalId() == null) {
            throw new IllegalArgumentException("Vui lòng chọn bệnh viện");
        }

        // Rank
        if (request.getRankLevel() != null && request.getRankLevel() <= 0) {
            throw new IllegalArgumentException("Cấp bậc phải là số nguyên dương");
        }

        // Ảnh
        if (request.getAvatarUrl() != null && request.getAvatarUrl().length() > 512) {
            throw new IllegalArgumentException("Đường dẫn ảnh không được vượt quá 512 ký tự");
        }
    }
}
