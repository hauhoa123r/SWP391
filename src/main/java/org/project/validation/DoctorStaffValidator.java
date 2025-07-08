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

        // ================================
        // 1. Email
        // ================================
        if (!StringUtils.hasText(request.getEmail())) {
            throw new IllegalArgumentException("Email không được để trống");
        }

        if (!request.getEmail().matches("^[A-Za-z0-9]+(?:\\.[A-Za-z0-9]+)*@gmail\\.com$")) {
            throw new IllegalArgumentException("Email phải thuộc miền gmail.com và không chứa ký tự đặc biệt");
        }

        if (request.getUserId() == null && userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại trong hệ thống");
        }

        // ================================
        // 2. Số điện thoại
        // ================================
        if (!StringUtils.hasText(request.getPhoneNumber())) {
            throw new IllegalArgumentException("Số điện thoại không được để trống");
        }

        if (!request.getPhoneNumber().matches("^[0-9]{10}$")) {
            throw new IllegalArgumentException("Số điện thoại phải gồm đúng 10 chữ số");
        }

        if (request.getUserId() == null && userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new IllegalArgumentException("Số điện thoại đã tồn tại trong hệ thống");
        }

        // ================================
        // 3. Họ tên
        // ================================
        if (!StringUtils.hasText(request.getFullName()) || request.getFullName().trim().length() < 5) {
            throw new IllegalArgumentException("Họ tên phải có ít nhất 5 ký tự");
        }

        // ================================
        // 4. Vai trò nhân sự
        // ================================
        if (!StringUtils.hasText(request.getStaffRole())) {
            throw new IllegalArgumentException("Vui lòng chọn vai trò nhân viên");
        }

        boolean isDoctor = "DOCTOR".equalsIgnoreCase(request.getStaffRole());

        // ================================
        // 5. Loại nhân viên
        // ================================
        if (!StringUtils.hasText(request.getStaffType())) {
            throw new IllegalArgumentException("Vui lòng chọn loại nhân viên (toàn thời gian / bán thời gian)");
        }

        // ================================
        // 6. Cấp bậc
        // ================================
        if (request.getRankLevel() == null || request.getRankLevel() <= 0 || request.getRankLevel() > 20) {
            throw new IllegalArgumentException("Cấp bậc phải là số nguyên dương từ 1 đến 20");
        }

        // ================================
        // 7. Bệnh viện (bắt buộc cho tất cả)
        // ================================
        if (request.getHospitalId() == null || request.getHospitalId() <= 0) {
            throw new IllegalArgumentException("Vui lòng chọn bệnh viện hợp lệ");
        }

        // ================================
        // 8. Phòng ban (bắt buộc nếu là bác sĩ)
        // ================================
        if (isDoctor && (request.getDepartmentId() == null || request.getDepartmentId() <= 0)) {
            throw new IllegalArgumentException("Bác sĩ bắt buộc phải có phòng ban");
        }

        if (request.getDepartmentId() != null && request.getDepartmentId() < 0) {
            throw new IllegalArgumentException("Mã phòng ban không hợp lệ");
        }

        // ================================
        // 9. Chức danh bác sĩ
        // ================================
        if (isDoctor && !StringUtils.hasText(request.getDoctorRank())) {
            throw new IllegalArgumentException("Vui lòng nhập học hàm/chức danh nếu vai trò là bác sĩ");
        }

        // ================================
        // 10. Đường dẫn ảnh (nếu có)
        // ================================
        if (StringUtils.hasText(request.getAvatarUrl())) {
            if (request.getAvatarUrl().length() > 512) {
                throw new IllegalArgumentException("Đường dẫn ảnh không được vượt quá 512 ký tự");
            }

            if (!request.getAvatarUrl().matches("^(https?|ftp)://[^\\s]+$")) {
                throw new IllegalArgumentException("Đường dẫn ảnh phải là URL hợp lệ (http, https, ftp)");
            }
        }
    }
}
