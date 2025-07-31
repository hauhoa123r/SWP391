import {DepartmentResponse} from "/templates/shared/assets/js/model/response/DepartmentResponse.js";
import {StaffEducationResponse} from "/templates/shared/assets/js/model/response/StaffEducationResponse.js";
import {StaffExperienceResponse} from "/templates/shared/assets/js/model/response/StaffExperienceResponse.js";
import {StaffQualificationResponse} from "/templates/shared/assets/js/model/response/StaffQualificationResponse.js";
import {StaffSkillResponse} from "/templates/shared/assets/js/model/response/StaffSkillResponse.js";
import {StaffSpecialityResponse} from "/templates/shared/assets/js/model/response/StaffSpecialityResponse.js";
import {FormatUtils} from "/templates/shared/assets/js/utils/format-utils.js";

export class StaffResponse {
    /**
     * Creates an instance of StaffResponse.
     * @param id
     * @param fullName
     * @param avatarUrl
     * @param {DepartmentResponse} departmentEntity
     * @param userEntityEmail
     * @param {StaffEducationResponse} staffEducationEntities
     * @param {StaffExperienceResponse} staffExperienceEntities
     * @param {StaffSkillResponse} staffSkillEntities
     * @param {StaffQualificationResponse} staffQualificationEntities
     * @param {StaffSpecialityResponse} staffSpecialityEntities
     * @param hospitalEntityName
     * @param reviewCount
     * @param averageRating
     * @param userEntityPhoneNumber
     * @param staffType
     * @param staffRole
     * @param hireDate
     * @param managerFullName
     */
    constructor(id, fullName, avatarUrl, departmentEntity, userEntityEmail, staffEducationEntities, staffExperienceEntities, staffSkillEntities, staffQualificationEntities, staffSpecialityEntities, hospitalEntityName, reviewCount, averageRating, userEntityPhoneNumber, staffType, staffRole, hireDate, managerFullName) {
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
        this.userEntityPhoneNumber = userEntityPhoneNumber || "";
        this.staffType = staffType || "";
        this.staffRole = staffRole || "";
        this.hireDate = hireDate || null; // Hire date can be null if not provided
        this.managerFullName = managerFullName || "Không có";
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
                json.averageRating,
                json.userEntityPhoneNumber,
                json.staffType,
                json.staffRole,
                json.hireDate,
                json.managerFullName
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

    setRenderStrategy(strategy) {
        this.renderStrategy = strategy;
        return this;
    }

    render(...args) {
        if (this.renderStrategy) {
            return this.renderStrategy(this, ...args);
        }
        throw new Error("Render strategy is not set for StaffResponse");
    }
}

/**
 * Render a staff response for admin view
 * @param {StaffResponse} staffResponse
 * @returns {string}
 */
export function renderStaffResponseForAdmin(staffResponse) {
    return `
    <tr class="item">
        <input type="hidden" name="id" value="${staffResponse.id}">
        <td data-name="id">${staffResponse.id}</td>
        <td>
            <div class="d-flex align-items-center">
                <img alt="avatar" src="${staffResponse.avatarUrl}"
                     style="width:36px; height:36px; border-radius:50%; object-fit:cover; margin-right:8px;">
                <span data-name="fullName" class="fw-semibold">${staffResponse.fullName}</span>
            </div>
        </td>
        <td data-name="userEntityEmail">${staffResponse.userEntityEmail}</td>
        <td data-name="userEntityPhoneNumber">${staffResponse.userEntityPhoneNumber}</td>
        <td data-name="hireDate" data-value="${staffResponse.hireDate}">${FormatUtils.formatDate(staffResponse.hireDate)}</td>
        <td data-name="departmentEntityId">${staffResponse.departmentEntity.name}</td>
        <td data-name="hospitalEntityId">${staffResponse.hospitalEntityName}</td>
        <td data-name="staffRole">${staffResponse.staffRole}</td>
        <th data-name="managerFullName">${staffResponse.managerFullName}</th>
        <td data-name="staffType" class="d-none">${staffResponse.staffType}</td>
        <td>
            <a title="Thăng chức lên quản lý" class="upgrade-button d-inline-block pe-2" href="">
            <span class="text-warning">
            <svg class="icon-24" width="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                                    <path d="M5 15.5L12 8.5L19 15.5" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"></path>
                                </svg>
                                </span>
            </a>
            <a aria-controls="editForm" class="edit-button d-inline-block"
               data-bs-toggle="offcanvas"
               href="#editForm">
<span class="text-success">
   <svg fill="none" height="16" viewBox="0 0 16 16" width="16"
        xmlns="http://www.w3.org/2000/svg">
      <path d="M9.31055 14.3321H14.75" stroke="currentColor" stroke-linecap="round"
            stroke-linejoin="round" stroke-width="1.5"/>
      <path clip-rule="evenodd"
            d="M8.58501 1.84609C9.16674 1.15084 10.2125 1.04889 10.9222 1.6188C10.9614 1.64972 12.2221 2.62909 12.2221 2.62909C13.0017 3.10039 13.244 4.10233 12.762 4.86694C12.7365 4.90789 5.60896 13.8234 5.60896 13.8234C5.37183 14.1192 5.01187 14.2938 4.62718 14.298L1.89765 14.3323L1.28265 11.7292C1.1965 11.3632 1.28265 10.9788 1.51978 10.683L8.58501 1.84609Z"
            fill-rule="evenodd"
            stroke="currentColor" stroke-linecap="round" stroke-linejoin="round"
            stroke-width="1.5"/>
      <path d="M7.26562 3.50073L11.3548 6.64108" stroke="currentColor" stroke-linecap="round"
            stroke-linejoin="round" stroke-width="1.5"/>
   </svg>
</span>
            </a>
            <a class="d-inline-block ps-2 delete-btn delete-button" href="#">
<span class="text-danger">
   <svg fill="none" height="16" viewBox="0 0 15 16" width="15"
        xmlns="http://www.w3.org/2000/svg">
      <path
              d="M12.4938 6.10107C12.4938 6.10107 12.0866 11.1523 11.8503 13.2801C11.7378 14.2963 11.1101 14.8918 10.0818 14.9106C8.12509 14.9458 6.16609 14.9481 4.21009 14.9068C3.22084 14.8866 2.60359 14.2836 2.49334 13.2853C2.25559 11.1388 1.85059 6.10107 1.85059 6.10107"
              stroke="currentColor" stroke-linecap="round" stroke-linejoin="round"
              stroke-width="1.5"/>
      <path d="M13.5312 3.67969H0.812744" stroke="currentColor" stroke-linecap="round"
            stroke-linejoin="round" stroke-width="1.5"/>
      <path
              d="M11.0804 3.67974C10.4917 3.67974 9.98468 3.26349 9.86918 2.68674L9.68693 1.77474C9.57443 1.35399 9.19343 1.06299 8.75918 1.06299H5.58443C5.15018 1.06299 4.76918 1.35399 4.65668 1.77474L4.47443 2.68674C4.35893 3.26349 3.85193 3.67974 3.26318 3.67974"
              stroke="currentColor" stroke-linecap="round" stroke-linejoin="round"
              stroke-width="1.5"/>
   </svg>
</span>
            </a>
            
        </td>
    </tr>
    `;
}
