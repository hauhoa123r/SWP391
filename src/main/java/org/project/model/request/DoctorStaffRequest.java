package org.project.model.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class DoctorStaffRequest {

    private Long userId;
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Email phải có định dạng hợp lệ")
    private String email;
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9]{10}$", message = "Số điện thoại phải gồm đúng 10 chữ số")
    private String phoneNumber;
    @NotBlank(message = "Họ tên không được để trống")
    @Size(min = 2, message = "Tên người dùng cần ít nhất 2 ký tự trở lên. Vui lòng nhập họ tên đầy đủ.")
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