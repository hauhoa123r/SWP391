package org.project.admin.dto.request;

import org.project.admin.enums.patients.BloodType;
import org.project.admin.enums.patients.Gender;
import org.project.admin.enums.patients.Relationship;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientRequest {

    @NotNull(message = "User ID không được để trống")
    private Long userId;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(02|08|09|05|00)\\d{8}$",
             message = "Số điện thoại phải có 10 số và bắt đầu bằng 02/08/09/05/00")
    private String phoneNumber;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email phải đúng định dạng")
    @Size(max = 255, message = "Email không được vượt quá 255 ký tự")
    private String email;

    @NotBlank(message = "Họ và tên không được để trống")
    @Size(min = 2, max = 100, message = "Họ và tên phải từ 2 đến 100 ký tự")
    private String fullName;

    private String avatarUrl;

    @NotNull(message = "Mối quan hệ không được để trống")
    private Relationship relationship;

    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự")
    private String address;

    @NotNull(message = "Giới tính không được để trống")
    private Gender gender;

    @NotNull(message = "Ngày sinh không được để trống")
    @Past(message = "Ngày sinh phải là ngày trong quá khứ")
    private LocalDate birthdate;

    private BloodType bloodType;
}
