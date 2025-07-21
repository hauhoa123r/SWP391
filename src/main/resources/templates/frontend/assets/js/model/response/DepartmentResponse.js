import {BaseResponse} from "/templates/frontend/assets/js/model/response/BaseResponse.js";

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

    setRenderStrategy(strategy) {
        this.renderStrategy = strategy;
        return this;
    }

    toHtml(...args) {
        if (this.renderStrategy) {
            return this.renderStrategy(this, ...args);
        }
        throw new Error("Render strategy is not set for DepartmentResponse");
    }
}

/**
 * Renders a department response for booking.
 * @param {DepartmentResponse} departmentResponse - The department response to render.
 * @returns {string}
 */
export function renderDepartmentResponseForBooking(departmentResponse) {
    return `
   <div class="col-sm-4 mt-sm-0 mb-4">
        <div class="form-check form-check-inline m-0 p-0 position-relative d-block box-checked h-100">
            <input class="form-check-input" id="department-${departmentResponse.id}" name="department"
                   type="radio" value="${departmentResponse.id}">
            <label class="form-check-label d-inline-block w-100 h-100"
                   for="department-${departmentResponse.id}">
                <div class="department-card p-0 overflow-hidden text-center">
                    <div class="department-banner position-relative">
                        <img alt="Orthopedic"
                             class="img-fluid w-100"
                             src="${departmentResponse.bannerUrl}"
                             style="height: 120px; object-fit: cover;">
                    </div>
                    <div class="p-3">
                        <h5 class="mb-1">${departmentResponse.name}</h5>
                        <p class="text-primary mb-2">"${departmentResponse.slogan}"</p>
                    </div>
                </div>
            </label>
        </div>
    </div>
   `;
}

/**
 * Renders a department response for list.
 * @param {DepartmentResponse} departmentResponse - The department response to render.
 * @returns {string}
 */
export function renderDepartmentResponseForList(departmentResponse) {
    return `
    <div class="col mt-5 text-center">
        <div class="border iq-fancy-box position-relative bg-white h-100">
            <div class="iq-img-area d-block m-auto">
                <img alt="icon" class="img-fluid w-100 h-100" loading="lazy" src="${departmentResponse.bannerUrl}">
            </div>
            <div class="iq-fancy-box-content mt-5">
                <h4 class="mb-2">${departmentResponse.name}</h4>
                <p class="text-body m-0">${departmentResponse.slogan}</p>
                <div class="fencybox-button mt-3 mt-md-5">
                    <div class="iq-btn-container">
                        <a class="iq-button iq-btn-link text-capitalize" href="/department/detail/${departmentResponse.id}">
                            Xem thêm
                            <span class="btn-link-icon">
                      <svg fill="none" height="10" viewBox="0 0 8 8" width="10"
                           xmlns="http://www.w3.org/2000/svg">
                         <path d="M7.32046 4.70834H4.74952V7.25698C4.74952 7.66734 4.41395 8 4 8C3.58605 8 3.25048 7.66734 3.25048 7.25698V4.70834H0.679545C0.293423 4.6687 0 4.34614 0 3.96132C0 3.5765 0.293423 3.25394 0.679545 3.21431H3.24242V0.673653C3.28241 0.290878 3.60778 0 3.99597 0C4.38416 0 4.70954 0.290878 4.74952 0.673653V3.21431H7.32046C7.70658 3.25394 8 3.5765 8 3.96132C8 4.34614 7.70658 4.6687 7.32046 4.70834Z"
                               fill="currentColor"></path>
                      </svg>
                   </span>
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
    `;
}

export function renderDepartmentResponseForSelect(departmentResponse, departmentId) {
    return `
    <option value="${departmentResponse.id}" ${departmentResponse.id === parseInt(departmentId) ? "selected" : ""}>
        ${departmentResponse.name}
    </option>
    `;

}