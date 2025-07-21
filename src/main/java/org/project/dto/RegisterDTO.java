package org.project.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.project.enums.FamilyRelationship;
import org.project.enums.Gender;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO {

    @NotBlank(message = "Vui lòng nhập email.")
    @Email(message = "Email không đúng định dạng.")
    private String email;

    @NotBlank(message = "Vui lòng nhập mật khẩu.")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự.")
    private String password;

    @NotBlank(message = "Vui lòng nhập số điện thoại.")
    @Pattern(regexp = "^(03|05|07|08|09)\\d{8}$", message = "Số điện thoại phải là số hợp lệ tại Việt Nam.")
    private String phoneNumber;

    @NotBlank(message = "Vui lòng nhập họ và tên.")
    private String fullName;

    @NotBlank(message = "Vui lòng nhập địa chỉ.")
    private String address;

    @NotBlank(message = "Vui lòng chọn ngày sinh.")
    private String birthdate;

    @NotNull(message = "Vui lòng chọn giới tính.")
    private Gender gender;

    @NotNull(message = "Vui lòng chọn quan hệ với bệnh nhân.")
    private FamilyRelationship familyRelationship;
}
