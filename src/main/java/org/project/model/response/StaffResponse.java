package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StaffResponse {
    private Long id;
    private String fullName;
    private String avatarUrl;
    private DepartmentResponse departmentEntity;
    private HospitalResponse hospitalEntity;
    private String userEntityEmail;
    private String userEntityPhoneNumber;
    private List<StaffEducationResponse> staffEducationEntities;
    private List<StaffExperienceResponse> staffExperienceEntities;
    private List<StaffSkillResponse> staffSkillEntities;
    private List<StaffQualificationResponse> staffQualificationEntities;
    private List<StaffSpecialityResponse> staffSpecialityEntities;
    private Date hireDate;
    private String managerFullName;
    private String hospitalEntityName;
    private Integer reviewCount;
    private Double averageRating;
    private String staffType;
    private String staffRole;
}
