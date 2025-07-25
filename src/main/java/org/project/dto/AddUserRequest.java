package org.project.dto;

import lombok.Data;
import org.project.enums.*;

import java.time.LocalDate;

@Data
public class AddUserRequest {
    private String email;
    private String phoneNumber;
    private String password;
    private UserRole role;
    private Boolean twoFactorEnabled = false;

    // patient info
    private PatientInfo patient;
    // staff info
    private StaffInfo staff;

    @Data
    public static class PatientInfo {
        private String fullName;
        private Gender gender;
        private LocalDate birthdate;
        private BloodType bloodType;
        private String address;
        private FamilyRelationship relationship;
    }

    @Data
    public static class StaffInfo {
        private String fullName;
        private StaffRole staffRole;
        private Long hospitalId;
        private Long departmentId;
        private LocalDate hireDate;
    }
}
