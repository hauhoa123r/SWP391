package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatientResponse {
    private Long id;
    private String phoneNumber;
    private String email;
    private String fullName;
    private String avatarUrl;
    private String address;
    private String birthdate;
    private Long age;
    private String familyRelationship;
    private String gender;
    private String bloodType;
}
