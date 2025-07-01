package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.project.enums.BloodType;
import org.project.enums.Gender;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminPatientResponse {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String address;
    private LocalDate dateOfBirth;
    private Integer age;

    // Alias for Thymeleaf template compatibility (patient.birthdate)
    public LocalDate getBirthdate() {
        return dateOfBirth;
    }
    private Gender gender;
    private String familyRelationship;
    private BloodType bloodType;
}
