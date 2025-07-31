package org.project.model.request;

import lombok.*;
import org.project.enums.BloodType;
import org.project.enums.Gender;

import java.time.LocalDate;

/**
 * DTO đại diện cho bộ lọc tìm kiếm bệnh nhân ở màn hình quản trị.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminPatientRequest {
    // Khoá tìm kiếm chung (họ tên / email / số điện thoại)
    private String keyword;

    // Lọc theo ID bệnh nhân hoặc người dùng
    private Long patientId;
    private Long userId;

    // Thông tin chi tiết
    private String fullName;
    private String phoneNumber;
    private String email;
    private String address;

    // Thuộc tính enum
    private Gender gender;
    private String familyRelationship;
    private BloodType bloodType;
    private LocalDate dateOfBirth;
    // Khoảng ngày sinh
    private LocalDate birthdateFrom;
    private LocalDate birthdateTo;
}
