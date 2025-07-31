package org.project.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.project.enums.operation.SortDirection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HospitalDTO {
    @NotBlank(message = "Tên bệnh viện không được để trống")
    private String name;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;

    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại phải bắt đầu bằng 0 và có 10 chữ số")
    private String phoneNumber;

    @Email(message = "Email không hợp lệ")
    private String email;
    private String avatarUrl;
    private String sortFieldName;
    private SortDirection sortDirection;
}
