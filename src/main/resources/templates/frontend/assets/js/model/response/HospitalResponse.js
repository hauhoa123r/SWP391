import {BaseResponse} from "/templates/frontend/assets/js/model/response/BaseResponse.js";

export class HospitalResponse extends BaseResponse {
    constructor(id, name, address, email, avatarUrl, phoneNumber) {
        super();
        this.id = id;
        this.name = name;
        this.address = address;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.phoneNumber = phoneNumber;
    }

    static fromJson(json) {
        return new HospitalResponse(
                json.id,
                json.name,
                json.address,
                json.email,
                json.avatarUrl,
                json.phoneNumber
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

/**
 * Renders a hospital response for list.
 * @param {HospitalResponse} hospitalResponse - The hospital response to render.
 * @returns {string}
 */
export function renderHospitalResponseForList(hospitalResponse) {
    return `
        <div class="col mb-4">
            <div class="h-100 border iq-blog blog-standard position-relative">
              <a href="/hospital/detail/${hospitalResponse.id}" class="blog-image d-block overflow-hidden">
                <img src="${hospitalResponse.avatarUrl}" alt="hospital-avatar" class="img-fluid w-100" loading="lazy">
              </a>
              <div class="iq-post-details bg-white p-4">
                <div class="blog-title mb-2">
                      <a href="/hospital/detail/${hospitalResponse.id}">
                        <h5 class="text-capitalize">${hospitalResponse.name}</h5>
                      </a>
                </div>
                <div class="iq-blog-meta mt-3">
                  <ul class="list-inline">
                    <li class="list-item text-uppercase">
                      <i class="fas fa-map-marker-alt me-1" aria-hidden="true"></i>
                      <span>${hospitalResponse.address}</span>
                    </li>
                    <li class="list-item text-uppercase">
                      <i class="fas fa-phone-alt me-1" aria-hidden="true"></i>
                      <span>${hospitalResponse.phoneNumber}</span>
                    </li>
                  </ul>
                </div>
                <div class="blog-button mt-3">
                  <div class="iq-btn-container">
                    <a class="iq-button iq-btn-link text-capitalize" href="/hospital/detail/${hospitalResponse.id}">
                      Xem chi tiết
                      <span class="btn-link-icon">
                        <svg xmlns="http://www.w3.org/2000/svg" width="10" height="10" viewBox="0 0 8 8" fill="none">
                          <path d="M7.32046 4.70834H4.74952V7.25698C4.74952 7.66734 4.41395 8 4 8C3.58605 8 3.25048 7.66734 3.25048 7.25698V4.70834H0.679545C0.293423 4.6687 0 4.34614 0 3.96132C0 3.5765 0.293423 3.25394 0.679545 3.21431H3.24242V0.673653C3.28241 0.290878 3.60778 0 3.99597 0C4.38416 0 4.70954 0.290878 4.74952 0.673653V3.21431H7.32046C7.70658 3.25394 8 3.5765 8 3.96132C8 4.34614 7.70658 4.6687 7.32046 4.70834Z" fill="currentColor"></path>
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