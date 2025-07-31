package org.project.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.project.enums.StaffRole;
import org.project.enums.StaffType;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StaffDTO extends SortBaseDTO {
    private Long userEntityId;
    private Long id;
    @NotBlank(message = "Tên nhân viên không được để trống")
    private String fullName;
    @Email(message = "Email không hợp lệ")
    private String email;
    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại phải bắt đầu bằng 0 và có 10 chữ số")
    private String phoneNumber;
    @NotNull(message = "Phòng ban không được để trống")
    private Long departmentEntityId;
    private String departmentEntityName;
    @NotNull(message = "Bệnh viện không được để trống")
    private Long hospitalEntityId;
    private String hospitalEntityName;
    private StaffRole staffRole;
    private StaffType staffType;
    private Date hireDate;
    private String managerFullName;
    private Integer reviewCount;
    private Double averageRating;
}
