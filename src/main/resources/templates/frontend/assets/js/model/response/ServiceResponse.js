import {BaseResponse} from "/templates/frontend/assets/js/model/response/BaseResponse.js";
import {ProductResponse} from "/templates/frontend/assets/js/model/response/ProductResponse.js";

export class ServiceResponse extends BaseResponse {
    constructor(id, productEntity) {
        super();
        this.id = id;
        this.productEntity = productEntity;
    }

    static fromJson(json) {
        const serviceResponse = new ServiceResponse(json.id, json.productEntity);

        // Convert nested object to its Response class
        if (json.productEntity) {
            serviceResponse.productEntity = ProductResponse.fromJson(json.productEntity);
        }

        return serviceResponse;
    }

    static fromJsonArray(jsonArray) {
        return jsonArray.map(json => ServiceResponse.fromJson(json));
    }

    setRenderStrategy(strategy) {
        this.renderStrategy = strategy;
        return this;
    }

    getRating() {
        let html = `
            <span class="ms-1 text-muted small">${Math.floor(this.productEntity.averageRating * 10) / 10}</span>
        `;
        for (let i = 1; i <= 5; i++) {
            html += `<span class="${this.productEntity.reviewCount ? "text-warning" : "text-primary"}">`;
            if (i < this.productEntity.averageRating + 0.5) {
                html += `<i class="fas fa-star"></i>`;
            }
            if (this.productEntity.averageRating > i && this.productEntity.averageRating <= i + 0.5) {
                html += `<i class="fas fa-star-half-alt"></i>`;
            }
            if (i >= this.productEntity.averageRating + 1) {
                html += `<i class="far fa-star"></i>`;
            }
            html += `</span>`;
        }
        return html;
    }

    toHtml(...args) {
        if (this.renderStrategy) {
            return this.renderStrategy(this, ...args);
        }
        throw new Error("Render strategy is not set for ServiceResponse");
    }
}

/**
 * Renders a service response for booking in HTML format.
 * @param {ServiceResponse} serviceResponse - The service response to render.
 * @returns {string} The HTML string representing the service response.
 */
export function renderServiceResponseForBooking(serviceResponse) {
    return `
    <div class="col-sm-4 mb-4">
        <div
                class="form-check form-check-inline m-0 p-0 position-relative d-block box-checked h-100 service">
            <input class="form-check-input" id="service-${serviceResponse.id}"
                   name="radios" type="radio" value="${serviceResponse.id}">
            <label
                    class="form-check-label d-inline-block overflow-hidden w-100 h-100"
                    for="service-${serviceResponse.id}">
                    <span class="d-block p-4 text-center position-relative">
                        <span class="d-block mb-3 position-relative">
                            <img alt="img"
                                 class="rounded-circle object-cover"
                                 height="70"
                                 src="${serviceResponse.productEntity.imageUrl}"
                                 width="70">
                        </span>
                        <span
                                class="d-block h6 fw-500 mt-3 mb-1 service-name">${serviceResponse.productEntity.name}</span>
                        <span class="text-body service-price">${serviceResponse.productEntity.price}$</span>
                    </span>
            </label>
        </div>
    </div>
    `;
}

/**
 * Renders a service response for list view in HTML format.
 * @param {ServiceResponse} serviceResponse - The service response to render.
 * @returns {string} The HTML string representing the service response for list view.
 */
export function renderServiceResponseForList(serviceResponse) {
    console.log("Rendering service response for list:", serviceResponse);
    return `
        <div class="col mt-5 text-center">
            <div class="iq-fancy-box position-relative p-md-5 bg-light rounded h-100">
                <div class="overflow-hidden mb-5">
                    <img class="img-fluid w-100 service-img" loading="lazy" src="${serviceResponse.productEntity.imageUrl}">
                </div>
                <div class="iq-fancy-box-content mt-5">
                    <h4 class="mb-2">${serviceResponse.productEntity.name}</h4>
                    <p class="text-body m-0 line-count-3">${serviceResponse.productEntity.description}</p>
                    <div class="d-flex justify-content-between align-items-center mt-3">
                        <div class="service-price fw-bold">${serviceResponse.productEntity.price}</div>
                        <div class="service-rating">${serviceResponse.getRating()}</div>
                    </div>
                    <div class="fencybox-button mt-3 mt-md-5">
                        <div class="iq-btn-container">
                            <a class="iq-button iq-btn-link text-capitalize" href="${"/service/detail/" + serviceResponse.id}">
                                read more
                                <span class="btn-link-icon">
                                      <svg fill="none" height="10" viewBox="0 0 8 8"
                                           width="10"
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