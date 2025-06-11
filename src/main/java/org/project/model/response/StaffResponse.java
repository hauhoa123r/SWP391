package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.project.entity.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StaffResponse {
    private Long id;
    private String fullName;
    private String avatarUrl;
    private String departmentEntityName;
    private List<StaffEducationEntity> staffEducationEntities;
    private List<StaffExperienceEntity> staffExperienceEntities;
    private List<StaffSkillEntity> staffSkillEntities;
    private List<StaffQualificationEntity> staffQualificationEntities;
    private List<StaffSpecialityEntity> staffSpecialityEntities;
    private String hospitalEntityName;
}
