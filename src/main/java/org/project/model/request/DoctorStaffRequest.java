package org.project.model.request;

import lombok.Data;

@Data
public class DoctorStaffRequest {

    private Long userId;
    private String email;
    private String phoneNumber;
    private String fullName;
    private String staffRole;
    private String staffType;
    private Integer rankLevel;
    private String avatarUrl;
    private Long departmentId;
    private Long hospitalId;
    private Long managerId;
    private String doctorRank;
}
