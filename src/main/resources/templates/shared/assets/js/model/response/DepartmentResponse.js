import {BaseResponse} from "/templates/shared/assets/js/model/response/BaseResponse.js";

export class DepartmentResponse extends BaseResponse {
    constructor(id, name, slogan, description, bannerUrl, videoUrl) {
        super();
        this.id = id;
        this.name = name;
        this.slogan = slogan;
        this.description = description;
        this.bannerUrl = bannerUrl;
        this.videoUrl = videoUrl;
    }

    static fromJson(json) {
        return new DepartmentResponse(
                json.id,
                json.name,
                json.slogan,
                json.description,
                json.bannerUrl,
                json.videoUrl
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

/**
 * Renders a department response for admin.
 * @param {DepartmentResponse} departmentResponse - The department response to render.
 * @returns {string}
 */
export function renderDepartmentResponseForAdmin(departmentResponse) {
    return `
   <tr class="item">
       <input type="hidden" name="id" value="${departmentResponse.id}">
        <td data-name="id">${departmentResponse.id}</td>
        <td data-name="bannerUrl" data-value="${departmentResponse.bannerUrl}">
            <img alt="banner" src="${departmentResponse.bannerUrl}"
                 style="width:100px;">
        </td>
        <td data-name="name">${departmentResponse.name}</td>
        <td data-name="slogan">${departmentResponse.slogan}</td>
        <td data-name="description" class="truncate">${departmentResponse.description}</td>
        <td data-name="videoUrl" data-value="${departmentResponse.videoUrl}">
            <video controls style="width:100px;">
                <source src="${departmentResponse.videoUrl}" type="video/mp4">
            </video>
        </td>
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