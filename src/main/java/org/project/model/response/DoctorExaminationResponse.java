package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorExaminationResponse {
    private Long appointmentId;
    private Long patientId;
    private String patientName;
    private String gender;
    private String email;
    private String phone;
    private String age;
    private String address;
    private String serviceName;
    private String avatarUrl;
    private List<String> allergies;
    private List<String> chronicDiseases;
    private String symptoms;
}
