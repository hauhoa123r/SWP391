import {BaseResponse} from "/frontend/assets/js/model/response/BaseResponse.js";

export class DepartmentResponse extends BaseResponse {
    constructor(id, name, slogan, description, bannerUrl) {
        super();
        this.id = id;
        this.name = name;
        this.slogan = slogan;
        this.description = description;
        this.bannerUrl = bannerUrl;
    }

    static fromJson(json) {
        return new DepartmentResponse(
            json.id,
            json.name,
            json.slogan,
            json.description,
            json.bannerUrl
        );
    }

    static fromJsonArray(jsonArray) {
        return jsonArray.map(json => DepartmentResponse.fromJson(json));
    }

    toHtml() {
        return `
        <div class="col-sm-4 mt-sm-0 mb-4">
            <div class="form-check form-check-inline m-0 p-0 position-relative d-block box-checked h-100">
                <input class="form-check-input" id="department-${this.id}" name="department"
                       type="radio" value="${this.id}">
                <label class="form-check-label d-inline-block w-100 h-100"
                       for="department-${this.id}">
                    <div class="department-card p-0 overflow-hidden text-center">
                        <div class="department-banner position-relative">
                            <img alt="Orthopedic"
                                 class="img-fluid w-100"
                                 src="${this.bannerUrl}"
                                 style="height: 120px; object-fit: cover;">
                        </div>
                        <div class="p-3">
                            <h5 class="mb-1">${this.name}</h5>
                            <p class="text-primary mb-2">"${this.slogan}"</p>
                        </div>
                    </div>
                </label>
            </div>
        </div>
        `;
    }
}
