package org.project.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.project.enums.BloodType;
import org.project.enums.Gender;

import java.sql.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppointmentDetailResponse {
    Long id;
    Long patientEntityId;
    String patientEntityPhoneNumber;
    String patientEntityEmail;
    String patientEntityFullName;
    String patientEntityAvatarUrl;
    String patientEntityAddress;
    Date patientEntityBirthdate;
//    BloodType patientEntityBloodType;
    Gender patientEntityGender;
    Integer durationMinutes;
    String medicalReportEntityMainComplaint;
//    public String getPatientEntityBloodTypeValue() {
//        return patientEntityBloodType != null ? patientEntityBloodType.getValue() : null;
//    }
}
