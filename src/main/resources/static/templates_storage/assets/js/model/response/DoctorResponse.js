import {BaseResponse} from "/frontend/assets/js/model/response/BaseResponse.js";
import {StaffResponse} from "/frontend/assets/js/model/response/StaffResponse.js";

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

    toHtml() {
        return `
        <div class="col-sm-6 mt-4">
            <div
                    class="form-check form-check-inline m-0 p-0 position-relative d-block box-checked h-100 doctor">
                <input class="form-check-input" id="doctor-${this.id}"
                       name="radios" type="radio" value="${this.staffEntity.id}">
                <label
                        class="form-check-label d-inline-block overflow-hidden w-100 h-100"
                        for="doctor-${this.id}">
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
                                     src="${this.staffEntity.avatarUrl}">
                            </span>
                            <span class="d-block h5 mb-2 doctor-name">${this.staffEntity.fullName}</span>
                            <span
                                    class="d-flex align-items-center gap-1 justify-content-between">
                                <span class="border d-inline-block w-25"></span>
                                <span
                                        class="bg-secondary px-3 py-2 rounded-pill text-white w-100">${this.doctorRank}</span>
                                <span class="border d-inline-block w-25"></span>
                            </span>
                            <span
                                    class="d-block h6 mt-3 mb-1 fw-500">Email</span>
                            <span
                                    class="text-body">${this.staffEntity.userEntityEmail}</span>
                        </span>
                </label>
            </div>
        </div>
        `;
    }
}
