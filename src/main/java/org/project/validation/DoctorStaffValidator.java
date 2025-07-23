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
        // 8. Validation theo từng role cụ thể
        // ================================
        validateByRole(request, isDoctor);

        // ================================
        // 9. Avatar URL (nếu có)
        // ================================
        if (StringUtils.hasText(request.getAvatarUrl())) {
            if (request.getAvatarUrl().length() > 500) {
                throw new IllegalArgumentException("URL ảnh đại diện quá dài (tối đa 500 ký tự)");
            }
            if (!request.getAvatarUrl().matches("^https?://.*\\.(jpg|jpeg|png|gif)$")) {
                throw new IllegalArgumentException("URL ảnh đại diện phải có định dạng hợp lệ (.jpg, .jpeg, .png, .gif)");
            }
        }
    }

    /**
     * Validation chi tiết theo từng role
     */
    private void validateByRole(DoctorStaffRequest request, boolean isDoctor) {
        String role = request.getStaffRole();
        
        switch (role.toUpperCase()) {
            case "DOCTOR":
                validateDoctorRole(request);
                break;
            case "TECHNICIAN":
                validateTechnicianRole(request);
                break;
            case "SCHEDULING_COORDINATOR":
                validateSchedulingCoordinatorRole(request);
                break;
            case "ADMINISTRATIVE_STAFF":
                validateAdministrativeStaffRole(request);
                break;
            case "SECURITY_GUARD":
                validateSecurityGuardRole(request);
                break;
            case "CLEANER":
                validateCleanerRole(request);
                break;
            default:
                throw new IllegalArgumentException("Vai trò nhân viên không hợp lệ: " + role);
        }
    }

    /**
     * Validation cho vai trò BÁC SĨ
     */
    private void validateDoctorRole(DoctorStaffRequest request) {
        // Bác sĩ BẮT BUỘC phải có phòng ban
        if (request.getDepartmentId() == null || request.getDepartmentId() <= 0) {
            throw new IllegalArgumentException("Bác sĩ bắt buộc phải chọn phòng ban");
        }
        
        // Bác sĩ BẮT BUỘC phải có chức danh
        if (!StringUtils.hasText(request.getDoctorRank())) {
            throw new IllegalArgumentException("Bác sĩ bắt buộc phải chọn chức danh (Thạc sĩ, Tiến sĩ, Chuyên khoa I/II, PGS.TS)");
        }
        
        // Bác sĩ phải có cấp bậc cao (từ 5 trở lên)
        if (request.getRankLevel() != null && request.getRankLevel() < 5) {
            throw new IllegalArgumentException("Bác sĩ phải có cấp bậc từ 5 trở lên");
        }
    }

    /**
     * Validation cho vai trò KỸ THUẬT VIÊN
     */
    private void validateTechnicianRole(DoctorStaffRequest request) {
        // Kỹ thuật viên BẮT BUỘC phải có phòng ban
        if (request.getDepartmentId() == null || request.getDepartmentId() <= 0) {
            throw new IllegalArgumentException("Kỹ thuật viên bắt buộc phải chọn phòng ban");
        }
        
        // Kỹ thuật viên KHÔNG được có chức danh bác sĩ
        if (StringUtils.hasText(request.getDoctorRank())) {
            throw new IllegalArgumentException("Kỹ thuật viên không được chọn chức danh bác sĩ");
        }
    }

    /**
     * Validation cho vai trò ĐIỀU PHỐI LỊCH TRÌNH
     */
    private void validateSchedulingCoordinatorRole(DoctorStaffRequest request) {
        // Điều phối viên có thể không cần phòng ban cụ thể
        // Nhưng KHÔNG được có chức danh bác sĩ
        if (StringUtils.hasText(request.getDoctorRank())) {
            throw new IllegalArgumentException("Điều phối viên không được chọn chức danh bác sĩ");
        }
    }

    /**
     * Validation cho vai trò NHÂN VIÊN HÀNH CHÍNH
     */
    private void validateAdministrativeStaffRole(DoctorStaffRequest request) {
        // Nhân viên hành chính KHÔNG được có chức danh bác sĩ
        if (StringUtils.hasText(request.getDoctorRank())) {
            throw new IllegalArgumentException("Nhân viên hành chính không được chọn chức danh bác sĩ");
        }
    }

    /**
     * Validation cho vai trò BẢO VỆ
     */
    private void validateSecurityGuardRole(DoctorStaffRequest request) {
        // Bảo vệ KHÔNG được có chức danh bác sĩ
        if (StringUtils.hasText(request.getDoctorRank())) {
            throw new IllegalArgumentException("Bảo vệ không được chọn chức danh bác sĩ");
        }
        
        // Bảo vệ KHÔNG cần phòng ban cụ thể
        // Có thể để trống departmentId
    }

    /**
     * Validation cho vai trò LAO CÔNG
     */
    private void validateCleanerRole(DoctorStaffRequest request) {
        // Lao công KHÔNG được có chức danh bác sĩ
        if (StringUtils.hasText(request.getDoctorRank())) {
            throw new IllegalArgumentException("Lao công không được chọn chức danh bác sĩ");
        }
        
        // Lao công KHÔNG cần phòng ban cụ thể
        // Có thể để trống departmentId

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
