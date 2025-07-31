package org.project.model.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.project.enums.Gender;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDTO {
    @Email(message = "Email không hợp lệ")
    private String email;
    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại phải bắt đầu bằng 0 và có 10 chữ số")
    private String phoneNumber;
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Mật khẩu chỉ được chứa chữ cái và số")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password;
    @NotBlank(message = "Họ và tên không được để trống")
    private String patientEntityFullName;
    @NotBlank(message = "Địa chỉ không được để trống")
    private String patientEntityAddress;
    @Past(message = "Ngày sinh phải là ngày trong quá khứ")
    private Date patientEntityBirthdate;
    private Gender patientEntityGender;
}
