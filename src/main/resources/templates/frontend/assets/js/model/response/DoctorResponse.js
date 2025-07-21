import {BaseResponse} from "/templates/frontend/assets/js/model/response/BaseResponse.js";
import {StaffResponse} from "/templates/frontend/assets/js/model/response/StaffResponse.js";

export class DoctorResponse extends BaseResponse {
    constructor(id, staffEntity, doctorRank) {
        super();
        this.id = id;
        this.staffEntity = staffEntity;
        this.doctorRank = doctorRank;
    }

    static fromJson(json) {
        const doctorResponse = new DoctorResponse(
                json.id,
                json.staffEntity,
                json.doctorRank
        );

        if (json.staffEntity) {
            doctorResponse.staffEntity = StaffResponse.fromJson(json.staffEntity);
        }

        return doctorResponse;
    }

    static fromJsonArray(jsonArray) {
        return jsonArray.map(json => DoctorResponse.fromJson(json));
    }

    setRenderStrategy(strategy) {
        this.renderStrategy = strategy;
        return this;
    }

    getRating() {
        return `
        <div class="share iq-team-social position-absolute">
            <div class="p-2 bg-light rounded">
                <div class="stars">
                    <i class="fas fa-star ${this.staffEntity.reviewCount ? "text-warning" : "text-primary"}"></i>
                    <span class="ms-1">${Math.floor(this.staffEntity.averageRating * 10) / 10}</span>
                </div>
                <small>${this.staffEntity.reviewCount ? (this.staffEntity.reviewCount + " Đánh giá") : "Chưa có đánh giá"}</small>
            </div>
        </div>
        `;
    }

    toHtml(...args) {
        if (this.renderStrategy) {
            return this.renderStrategy(this, ...args);
        }
        throw new Error("Render strategy is not set for DoctorResponse");
    }
}

/**
 * Render a doctor response for booking
 * @param {DoctorResponse} doctorResponse - The doctor response object to render
 * @returns {string}
 */
export function renderDoctorResponseForBooking(doctorResponse) {
    return `
    <div class="col-sm-6 mt-4">
        <div
            class="form-check form-check-inline m-0 p-0 position-relative d-block box-checked h-100 doctor">
            <input class="form-check-input" id="doctor-${doctorResponse.id}"
                   name="radios" type="radio" value="${doctorResponse.staffEntity.id}"/>
            <label
                class="form-check-label d-inline-block overflow-hidden w-100 h-100"
                for="doctor-${doctorResponse.id}">
                    <span
                        class="d-block appointment-doctor-box p-4 text-center position-relative">
                        <span
                            class="d-block bg-light py-5 position-absolute top-0 start-0 end-0">
                            <span class="py-1"></span>
                        </span>
                        <span class="d-block mb-3 position-relative">
                            <img alt="img"
                                 class="rounded-circle object-cover p-1 bg-white"
                                 height="80"
                                 width="80"
                                 src="${doctorResponse.staffEntity.avatarUrl}"/>
                        </span>
                        <span class="d-block h5 mb-2 doctor-name">${doctorResponse.staffEntity.fullName}</span>
                        <span
                            class="d-flex align-items-center gap-1 justify-content-between">
                            <span class="border d-inline-block w-25"></span>
                            <span
                                class="bg-secondary px-3 py-2 rounded-pill text-white w-100">${doctorResponse.doctorRank}</span>
                            <span class="border d-inline-block w-25"></span>
                        </span>
                        <span
                            class="d-block h6 mt-3 mb-1 fw-500">Email</span>
                        <span
                            class="text-body">${doctorResponse.staffEntity.userEntityEmail}</span>
                    </span>
            </label>
        </div>
    </div>
    `;
}

/**
 * Render a doctor response for appointment list
 * @param {DoctorResponse} doctorResponse - The doctor response object to render
 * @returns {string}
 */
export function renderDoctorResponseForList(doctorResponse) {
    return `
    <div class="col mb-5 cursor-pointer">
        <div class="iq-team-block team-overdetail position-relative p-2"
             onclick="window.location.href='/doctor/detail/${doctorResponse.id}'">
            <div class="iq-team-img">
                <img alt="Avatar Doctor"
                     class="img-fluid w-100"
                     loading="lazy"
                     onerror="this.onerror=null;this.src='/assets/images/error/mr.nobody.jpg'"
                     src="${doctorResponse.staffEntity.avatarUrl}">
            </div>
            <div>${doctorResponse.getRating()}</div>
            <div class="iq-team-info position-absolute d-block w-100">
                <div class="iq-team-main-detail bg-white">
                    <h5>${doctorResponse.staffEntity.fullName}</h5>
                    <p class="mb-0 text-uppercase fw-bolder text-primary fw-500">
                        ${doctorResponse.staffEntity.departmentEntity?.name || ""}
                    </p>
                </div>
            </div>
        </div>
    </div>
    `;
}
