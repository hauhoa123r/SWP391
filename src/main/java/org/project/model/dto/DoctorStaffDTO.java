package org.project.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorStaffDTO {

    // USER info
    private String email;
    private String password;
    private String phoneNumber;
    private String fullName;
    private String avatarUrl;
    private String gender;
    private String address;

    // STAFF info
    private String staffRole;   // DOCTOR / STAFF
    private String staffType;
    private Integer rankLevel;
    private String doctorRank;

    private Long departmentId;
    private Long hospitalId;
    private Long managerId;
}
