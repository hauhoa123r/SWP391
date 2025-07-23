package org.project.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.project.enums.*;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPatientDTO {
    // From UserEntity
    private Long userId;
    private String email;
    private String phoneNumber;
    private UserRole userRole;
    private UserStatus userStatus;
    private Boolean isVerified;
    private Boolean twoFactorEnabled;

    // From PatientEntity
    private String fullName;
    private String avatarUrl;
    private String address;
    private Date birthdate;
    private Gender gender;
    private FamilyRelationship familyRelationship;
    private BloodType bloodType;
}
