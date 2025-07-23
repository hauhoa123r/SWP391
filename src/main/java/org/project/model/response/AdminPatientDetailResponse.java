package org.project.model.response;

import lombok.Builder;
import lombok.Data;
import org.project.enums.BloodType;
import org.project.enums.FamilyRelationship;
import org.project.enums.Gender;

import java.time.LocalDate;
import java.util.Date;

@Data
@Builder
public class AdminPatientDetailResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private LocalDate dateOfBirth;
    private Gender gender;
    private FamilyRelationship familyRelationship;
    private BloodType bloodType;
    private String avatarUrl;
    private Integer age;
}
