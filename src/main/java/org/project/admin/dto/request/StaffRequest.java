package org.project.admin.dto.request;

import org.project.admin.enums.staffs.StaffRole;
import org.project.admin.enums.staffs.StaffType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StaffRequest {

    @NotNull(message = "User ID không được để trống")
    private Long userId;

    @NotNull(message = "Vai trò nhân viên không được để trống")
    private StaffRole staffRole;

    private Long managerId;

    @NotNull(message = "Phòng ban không được để trống")
    private Long departmentId;

    @NotBlank(message = "Họ và tên không được để trống")
    @Size(min = 2, max = 100, message = "Họ và tên phải từ 2 đến 100 ký tự")
    private String fullName;

    private String avatarUrl;

    @NotNull(message = "Ngày vào làm không được để trống")
    @Past(message = "Ngày vào làm phải là ngày trong quá khứ")
    private LocalDate hireDate;

    @Min(value = 1, message = "Cấp bậc phải từ 1 đến 7")
    @Max(value = 7, message = "Cấp bậc phải từ 1 đến 7")
    private int rankLevel;

    @NotNull(message = "Loại nhân viên không được để trống")
    private StaffType staffType;

    @NotNull(message = "Bệnh viện không được để trống")
    private Long hospitalId;
}
