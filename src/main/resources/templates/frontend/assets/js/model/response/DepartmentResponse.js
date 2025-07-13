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

export function renderDepartmentResponseForList(departmentResponse, departmentId) {
    return `
    <button 
        aria-selected="false" 
        class="nav-link${departmentResponse.id === parseInt(departmentId) ? " active" : ""}"
        data-department="${departmentResponse.id}"
        type="button">
        ${departmentResponse.name}
    </button>
    `;
}

export function renderDepartmentResponseForSelect(departmentResponse, departmentId) {
    return `
    <option value="${departmentResponse.id}" ${departmentResponse.id === parseInt(departmentId) ? "selected" : ""}>
        ${departmentResponse.name}
    </option>
    `;

}