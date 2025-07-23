import {DepartmentResponse} from "/templates/frontend/assets/js/model/response/DepartmentResponse.js";
import {StaffEducationResponse} from "/templates/frontend/assets/js/model/response/StaffEducationResponse.js";
import {StaffExperienceResponse} from "/templates/frontend/assets/js/model/response/StaffExperienceResponse.js";
import {StaffQualificationResponse} from "/templates/frontend/assets/js/model/response/StaffQualificationResponse.js";
import {StaffSkillResponse} from "/templates/frontend/assets/js/model/response/StaffSkillResponse.js";
import {StaffSpecialityResponse} from "/templates/frontend/assets/js/model/response/StaffSpecialityResponse.js";

export class StaffResponse {
    constructor(id, fullName, avatarUrl, departmentEntity, userEntityEmail, staffEducationEntities, staffExperienceEntities, staffSkillEntities, staffQualificationEntities, staffSpecialityEntities, hospitalEntityName, reviewCount, averageRating) {
        this.id = id;
        this.fullName = fullName;
        this.avatarUrl = avatarUrl;
        this.departmentEntity = departmentEntity;
        this.userEntityEmail = userEntityEmail;
        this.staffEducationEntities = staffEducationEntities;
        this.staffExperienceEntities = staffExperienceEntities;
        this.staffSkillEntities = staffSkillEntities;
        this.staffQualificationEntities = staffQualificationEntities;
        this.staffSpecialityEntities = staffSpecialityEntities;
        this.hospitalEntityName = hospitalEntityName;
        this.reviewCount = reviewCount;
        this.averageRating = averageRating;
    }

    static fromJson(json) {
        const staffResponse = new StaffResponse(
                json.id,
                json.fullName,
                json.avatarUrl,
                json.departmentEntity,
                json.userEntityEmail,
                json.staffEducationEntities,
                json.staffExperienceEntities,
                json.staffSkillEntities,
                json.staffQualificationEntities,
                json.staffSpecialityEntities,
                json.hospitalEntityName,
                json.reviewCount,
                json.averageRating
        );

        // Convert nested objects to their respective response classes
        if (json.departmentEntity) {
            staffResponse.departmentEntity = DepartmentResponse.fromJson(json.departmentEntity);
        }
        if (json.staffEducationEntities) {
            staffResponse.staffEducationEntities = StaffEducationResponse.fromJsonArray(json.staffEducationEntities);
        }
        if (json.staffExperienceEntities) {
            staffResponse.staffExperienceEntities = StaffExperienceResponse.fromJsonArray(json.staffExperienceEntities);
        }
        if (json.staffSkillEntities) {
            staffResponse.staffSkillEntities = StaffSkillResponse.fromJsonArray(json.staffSkillEntities);
        }
        if (json.staffQualificationEntities) {
            staffResponse.staffQualificationEntities = StaffQualificationResponse.fromJsonArray(json.staffQualificationEntities);
        }
        if (json.staffSpecialityEntities) {
            staffResponse.staffSpecialityEntities = StaffSpecialityResponse.fromJsonArray(json.staffSpecialityEntities);
        }

        return staffResponse;
    }

    static fromJsonArray(jsonArray) {
        return jsonArray.map(json => StaffResponse.fromJson(json));
    }
}
