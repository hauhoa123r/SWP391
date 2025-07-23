package org.project.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.project.enums.BloodType;
import org.project.enums.FamilyRelationship;
import org.project.enums.Gender;

import java.time.LocalDate;

@Getter
@Setter
public class AdminPatientUpdateRequest {
    @NotNull
    private Long userId;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String address;
    private LocalDate dateOfBirth;
    private Gender gender;
    private FamilyRelationship familyRelationship;
    private BloodType bloodType;
}