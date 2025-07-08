import {BaseResponse} from "/templates/frontend/assets/js/model/response/BaseResponse.js";

export class HospitalResponse extends BaseResponse {
    constructor(id, name, address, email, avatarUrl) {
        super();
        this.id = id;
        this.name = name;
        this.address = address;
        this.email = email;
        this.avatarUrl = avatarUrl;
    }

    static fromJson(json) {
        return new HospitalResponse(
            json.id,
            json.name,
            json.address,
            json.email,
            json.avatarUrl
        );
    }

    static fromJsonArray(jsonArray) {
        return jsonArray.map(json => HospitalResponse.fromJson(json));
    }

    setRenderStrategy(strategy) {
        this.renderStrategy = strategy;
        return this;
    }

    toHtml(...args) {
        if (this.renderStrategy) {
            return this.renderStrategy(this, ...args);
        }
        throw new Error("Render strategy is not set for HospitalResponse");
    }
}

/**
 * Renders a hospital response for booking in HTML format.
 * @param {HospitalResponse} hospitalResponse - The hospital response to render.
 * @returns {string}
 */
export function renderHospitalResponseForBooking(hospitalResponse) {
    return `
    <div class="col-sm-6 mt-sm-0 mb-4">
        <div class="form-check form-check-inline m-0 p-0 position-relative d-block box-checked h-100 hospital">
           <input type="radio" name="hospital" class="form-check-input" id="hospital-${hospitalResponse.id}" value="${hospitalResponse.id}">
           <label class="form-check-label d-inline-block w-100 h-100" for="hospital-${hospitalResponse.id}">
           <span class="d-block appointment-clinic-box p-4 text-center">
           <span class="d-block mb-4">
           <img alt="img" src="${hospitalResponse.avatarUrl}" height="80" width="80" class="rounded-circle object-cover">
           </span>
           <span class="d-block h5 mb-2 hospital-name">${hospitalResponse.name}</span>
           <span class="text-body hospital-address">${hospitalResponse.address}</span>
           <span class="d-block h6 mt-3 mb-1 fw-500">Email</span>
           <span class="text-body">${hospitalResponse.email}</span>
           </span>
           </label>
        </div>
     </div>
    `;
}
