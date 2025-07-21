package org.project.model.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
<<<<<<< HEAD
@Getter
@Setter
public class PatientDTO extends AbstractServiceAIDTO{
=======
public class PatientDTO extends AbstractServiceDTO {
>>>>>>> 1fe7b34939d6bcd04ae5de38ce13891189c4ebc0
    private Long userId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private String gender;
    private String dateOfBirth;
    private String familyRelationship;
    private String avatarBase64;
    private String bloodType;


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
