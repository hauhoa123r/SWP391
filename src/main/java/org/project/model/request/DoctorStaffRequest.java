package org.project.model.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class DoctorStaffRequest {

    private Long userId;
    @NotBlank(message = "Email không được để trống")
    @Pattern(regexp = "^[A-Za-z0-9]+(?:\\.[A-Za-z0-9]+)*@gmail\\.com$", message = "Email phải thuộc miền gmail.com và không chứa ký tự đặc biệt")
    private String email;
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9]{10}$", message = "Số điện thoại phải gồm đúng 10 chữ số")
    private String phoneNumber;
    @NotBlank(message = "Họ tên không được để trống")
    @Size(min = 5, message = "Họ tên phải có ít nhất 5 ký tự")
    private String fullName;
    @NotBlank(message = "Vui lòng chọn vai trò nhân viên")
    private String staffRole;
    @NotBlank(message = "Vui lòng chọn loại nhân viên")
    private String staffType;
    @NotNull(message = "Cấp bậc không được để trống")
    @Min(value = 1, message = "Cấp bậc phải từ 1 đến 20")
    @Max(value = 20, message = "Cấp bậc phải từ 1 đến 20")
    private Integer rankLevel;
    private String avatarUrl;
    private Long departmentId;
    @NotNull(message = "Vui lòng chọn bệnh viện hợp lệ")
    private Long hospitalId;
    private Long managerId;
    private String doctorRank;
}
