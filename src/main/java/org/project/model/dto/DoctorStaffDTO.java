package org.project.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorStaffDTO {
    
        @NotBlank(message = "Họ tên không được để trống")
        private String fullName;
    
        @Email(message = "Email không hợp lệ")
        @NotBlank(message = "Email là bắt buộc")
        private String email;
    
        @Pattern(regexp = "^[0-9]{10}$", message = "Số điện thoại phải gồm 10 chữ số")
        private String phoneNumber;
    
        @NotBlank(message = "Vai trò không được để trống")
        private String staffRole;
    
        @NotBlank(message = "Loại nhân viên không được để trống")
        private String staffType;
    
        @Min(value = 1, message = "Cấp bậc phải từ 1")
        @Max(value = 10, message = "Cấp bậc tối đa là 10")
        private Integer rankLevel;
    
        private String avatarUrl;
    
        private String doctorRank;
    
        @NotNull(message = "Vui lòng chọn mã bệnh viện")
        private Long hospitalId;
    
        private Long departmentId;
    
        private Long managerId;
    
        // Getters and Setters...
    }
    

