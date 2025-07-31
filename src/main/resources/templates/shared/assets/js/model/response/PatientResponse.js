import {BaseResponse} from "/templates/shared/assets/js/model/response/BaseResponse.js";
import {FormatUtils} from "/templates/shared/assets/js/utils/format-utils.js";

export class PatientResponse extends BaseResponse {
    constructor(id, phoneNumber, email, fullName, avatarUrl, address, birthdate, familyRelationship, gender, bloodType) {
        super();
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.fullName = fullName;
        this.avatarUrl = avatarUrl || "/frontend/assets/images/error/mr.nobody.jpg";
        this.address = address;
        this.birthdate = birthdate;
        this.familyRelationship = familyRelationship;
        this.gender = gender;
        this.bloodType = bloodType || "Chưa xác định";
    }

    static fromJson(json) {
        return new PatientResponse(
                json.id,
                json.phoneNumber,
                json.email,
                json.fullName,
                json.avatarUrl,
                json.address,
                json.birthdate,
                json.familyRelationship,
                json.gender,
                json.bloodType
        );
    }

    static fromJsonArray(jsonArray) {
        return jsonArray.map(json => PatientResponse.fromJson(json));
    }

    setRenderStrategy(strategy) {
        this.renderStrategy = strategy;
        return this;
    }

    toHtml(...args) {
        if (this.renderStrategy) {
            return this.renderStrategy(this, ...args);
        }
        throw new Error("Render strategy is not set for PatientResponse");
    }
}

/**
 * Render a patient response for booking
 * @param {PatientResponse} patientResponse - The patient response object to render
 * @returns {string}
 */
export function renderPatientResponseForBooking(patientResponse) {
    return `
        <div class="col-sm-k mt-4">
            <div class="form-check form-check-inline m-0 p-0 position-relative d-block box-checked patient">
                <input type="radio" name="patient" class="form-check-input" id="patient-${patientResponse.id}" value="${patientResponse.id}">
                <label class="form-check-label d-inline-block overflow-hidden w-100" for="patient-${patientResponse.id}">
                    <span class="d-block appointment-patient-box p-4 position-relative">
                        <div class="row">
                            <div class="col-md-4 text-center">
                                <span class="d-block mb-3 position-relative">
                                    <img alt="Ảnh bệnh nhân" src="${patientResponse.avatarUrl}" height="100" width="100" class="rounded-circle object-cover p-1 bg-white">
                                </span>
                                <div class="mb-1">
                                    <span class="h5 patient-name">${patientResponse.fullName}</span>
                                    <span class="text-body fst-italic">(${patientResponse.familyRelationship})</span>
                                </div>
                                <span class="bg-info px-3 py-2 d-inline-block rounded-pill text-white mb-2">Nhóm máu: ${patientResponse.bloodType}</span>
                                <span class="d-block text-body mb-2">${patientResponse.gender}</span>
                            </div>
                            <div class="col-md-8">
                                <div class="d-block patient-info mt-md-0 mt-3">
                                    <div class="row mb-2">
                                        <div class="col-sm-4">
                                            <span class="fw-medium text-secondary">Email:</span>
                                        </div>
                                        <div class="col-sm-8">
                                            <span class="text-body patient-email">${patientResponse.email}</span>
                                        </div>
                                    </div>
                                    <div class="row mb-2">
                                        <div class="col-sm-4">
                                            <span class="fw-medium text-secondary">Số điện thoại:</span>
                                        </div>
                                        <div class="col-sm-8">
                                            <span class="text-body patient-phone">${patientResponse.phoneNumber}</span>
                                        </div>
                                    </div>
                                    <div class="row mb-2">
                                        <div class="col-sm-4">
                                            <span class="fw-medium text-secondary">Địa chỉ:</span>
                                        </div>
                                        <div class="col-sm-8">
                                            <span class="text-body">${patientResponse.address}</span>
                                        </div>
                                    </div>
                                    <div class="row mb-2">
                                        <div class="col-sm-4">
                                            <span class="fw-medium text-secondary">Ngày sinh:</span>
                                        </div>
                                        <div class="col-sm-8">
                                            <span class="text-body">${patientResponse.birthdate}</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </span>
                </label>
            </div>
        </div>
        `;
}

/**
 * Render a patient response for admin
 * @param {PatientResponse} patientResponse - The patient response object to render
 * @returns {string}
 */
export function renderPatientResponseForAdmin(patientResponse) {
    return `
    <tr data-item="list" class="item">
        <input type="hidden" name="id" value="${patientResponse.id}">
        <td data-name="id">${patientResponse.id}</td>
        <td data-name="fullName">
            <h6 class="mb-0 text-body fw-normal">${patientResponse.fullName}</h6>
        </td>
        <td data-name="dateOfBirth" data-value="${patientResponse.birthdate}">${FormatUtils.formatDate(patientResponse.birthdate)}</td>
        <td data-name="phoneNumber">${patientResponse.phoneNumber}</td>
        <td data-name="email">${patientResponse.email}</td>
        <td data-name="gender">${patientResponse.gender}</td>
        <td data-name="bloodType">${patientResponse.bloodType}</td>
        <td data-name="address">${patientResponse.address}</td>
        <td data-name="familyRelationship">${patientResponse.familyRelationship}</td>
        <td>
            <a aria-controls="editForm" class="edit-button d-inline-block pe-2"
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