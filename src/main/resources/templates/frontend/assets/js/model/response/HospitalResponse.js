import {BaseResponse} from "/frontend/assets/js/model/response/BaseResponse.js";

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

    toHtml() {
        return `
        <div class="col-sm-6 mt-sm-0 mb-4">
            <div class="form-check form-check-inline m-0 p-0 position-relative d-block box-checked h-100 hospital">
               <input type="radio" name="hospital" class="form-check-input" id="hospital-${this.id}" value="${this.id}">
               <label class="form-check-label d-inline-block w-100 h-100" for="hospital-${this.id}">
               <span class="d-block appointment-clinic-box p-4 text-center">
               <span class="d-block mb-4">
               <img alt="img" src="${this.avatarUrl}" height="80" width="80" class="rounded-circle object-cover">
               </span>
               <span class="d-block h5 mb-2 hospital-name">${this.name}</span>
               <span class="text-body hospital-address">${this.address}</span>
               <span class="d-block h6 mt-3 mb-1 fw-500">Email</span>
               <span class="text-body">${this.email}</span>
               </span>
               </label>
            </div>
         </div>
        `;
    }
}
