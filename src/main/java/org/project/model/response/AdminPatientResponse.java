package org.project.model.response;

import lombok.*;
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
    private java.time.LocalDate dateOfBirth;
    private String email;
    private String address;
    private Gender gender;
    private String familyRelationship;
    private BloodType bloodType;

}
