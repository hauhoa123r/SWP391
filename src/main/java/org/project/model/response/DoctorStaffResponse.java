package org.project.model.response;
import lombok.Data;

@Data
public class DoctorStaffResponse {
    private Long staffId;

    private String fullName;
    private String staffRole;
    private String staffType;
    private Integer rankLevel;
    private String hireDate;
    private String avatarUrl;

    // User info
    private Long userId;
    private String email;
    private String phoneNumber;

    // Department
    private Long departmentId;
    private String departmentName;

    // Hospital
    private Long hospitalId;
    private String hospitalName;

    // Manager
    private Long managerId;
    private String managerName;

    // Doctor info (chỉ có nếu là bác sĩ)
    private String doctorRank;

}