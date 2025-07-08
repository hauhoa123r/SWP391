package org.project.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminStaffResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String departmentName;
    private String hospitalName;
    private String managerName;
    private String staffRole;
    private String staffType;
    private Integer rankLevel;
    private Double averageRating;
    private Integer reviewCount;
}
