package org.project.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PatientDTO extends AbstractServiceDTO {
    private Long userId;
    @NotBlank(message = "Họ và tên không được để trống")
    private String fullName;
    @Email(message = "Email không hợp lệ")
    private String email;
    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại phải bắt đầu bằng 0 và có 10 chữ số")
    private String phoneNumber;
    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;
    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$", message = "Giới tính không hợp lệ")
    private String gender;
    @Past(message = "Ngày sinh phải là ngày trong quá khứ")
    private String dateOfBirth;
    @Pattern(regexp = "^(SELF|FATHER|MOTHER|HUSBAND|BROTHER|SISTER|WIFE|SON|DAUGHTER|GRAND_FATHER|GRAND_MOTHER|COUSIN|AUNT|UNCLE|OTHER)$", message = "Mối quan hệ gia đình không hợp lệ")
    private String familyRelationship;
    private String avatarBase64;
    private String bloodType;
    private String sortFieldName;
    private String sortDirection;

    public String getAvatarBase64() {
        if (avatarBase64 != null && avatarBase64.startsWith("data:")) {
            String[] parts = avatarBase64.split(",");
            if (parts.length > 1) {
                return parts[1];
            }
        }
        return null;
    }

    public void setAvatarBase64(String avatarBase64) {
        this.avatarBase64 = avatarBase64;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }
}
