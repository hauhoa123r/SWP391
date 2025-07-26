package org.project.admin.dto.request;

import org.project.admin.enums.users.UserRole;
import org.project.admin.enums.users.UserStatus;
import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class UserRequest {
    private UserRole userRole;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(02|08|09|05|00)\\d{8}$",
             message = "Số điện thoại phải có 10 số và bắt đầu bằng 02/08/09/05/00")
    private String phoneNumber;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email phải đúng định dạng")
    @Size(max = 255, message = "Email không được vượt quá 255 ký tự")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password; // Plaintext for register/update

    private UserStatus userStatus;
    private Boolean twoFactorEnabled;
}
