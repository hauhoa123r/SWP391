package org.project.model.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AddUserRequestDTO {
    // ===== USERS TABLE =====
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Size(min = 10, max = 10, message = "Phone number must be 10 digits")
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "User role is required")
    private String userRole; // ADMIN / PATIENT / STAFF

    private String userStatus = "ACTIVE"; // default

    // ===== STAFF FIELDS =====
    private String fullName;
    private String avatarUrl;
    private String staffRole;    // e.g., DOCTOR
    private String staffType;    // FULL_TIME
    private Integer rankLevel;
    private LocalDate hireDate;
    private Long departmentId;
    private Long hospitalId;
    private Long managerId;

    // ===== PATIENT FIELDS =====
    private String gender;
    private String address;
    private String bloodType;
    private String relationship;
    private LocalDate birthdate;
}
