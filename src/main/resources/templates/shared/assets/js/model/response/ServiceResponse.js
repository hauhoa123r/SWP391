import {BaseResponse} from "/templates/shared/assets/js/model/response/BaseResponse.js";
import {DepartmentResponse} from "/templates/shared/assets/js/model/response/DepartmentResponse.js";
import {ProductResponse} from "/templates/shared/assets/js/model/response/ProductResponse.js";
import {ServiceFeatureResponse} from "/templates/shared/assets/js/model/response/ServiceFeatureResponse.js";
import {FormatUtils} from "/templates/shared/assets/js/utils/format-utils.js";

export class ServiceResponse extends BaseResponse {
    /**
     * Constructor for ServiceResponse
     * @param id
     * @param {ProductResponse} productEntity
     * @param {DepartmentResponse} departmentEntity
     * @param {ServiceFeatureResponse[]} serviceFeatureEntities
     */
    constructor(id, productEntity, departmentEntity, serviceFeatureEntities = []) {
        super();
        this.id = id;
        this.productEntity = productEntity;
        this.departmentEntity = departmentEntity;
        this.serviceFeatureEntities = serviceFeatureEntities;
    }

    static fromJson(json) {
        const serviceResponse = new ServiceResponse(json.id, json.productEntity);

        // Convert nested object to its Response class
        if (json.productEntity) {
            serviceResponse.productEntity = ProductResponse.fromJson(json.productEntity);
        }

        if (json.departmentEntity) {
            serviceResponse.departmentEntity = DepartmentResponse.fromJson(json.departmentEntity);
        }

        if (json.serviceFeatureEntities) {
            serviceResponse.serviceFeatureEntities = ServiceFeatureResponse.fromJsonArray(json.serviceFeatureEntities);
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

    formatCurrency(amount) {
        return (Number)(amount).toLocaleString("vi-VN", {
            style: "currency",
            currency: "VND",
            minimumFractionDigits: 0,
            maximumFractionDigits: 0
        });
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
                        <span class="text-body service-price">${serviceResponse.formatCurrency(serviceResponse.productEntity.price)}</span>
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
                        <div class="service-price fw-bold">${serviceResponse.formatCurrency(serviceResponse.productEntity.price)}</div>
                        <div class="service-rating">${serviceResponse.getRating()}</div>
                    </div>
                    <div class="fencybox-button mt-3 mt-md-5">
                        <div class="iq-btn-container">
                            <a class="iq-button iq-btn-link text-capitalize" href="${"/service/detail/" + serviceResponse.id}">
                                Xem thêm
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

/**
 * Renders a service response for admin view in HTML format.
 * @param {ServiceResponse} serviceResponse
 * @return {string} The HTML string representing the service response for admin view.
 */
export function renderServiceResponseForAdmin(serviceResponse) {
    return `
    <tr class="item">
        <input type="hidden" name="id" value="${serviceResponse.id}">
        <td class="d-none" data-name="productEntityProductAdditionalInfoEntities" data-value="${JSON.stringify(serviceResponse.productEntity.productAdditionalInfoEntities).replace(/"/g, "&quot;")}"></td>
        <td class="d-none" data-name="serviceFeatureEntities" data-value="${JSON.stringify(serviceResponse.serviceFeatureEntities).replace(/"/g, "&quot;")}"></td>
        <td data-name="id">${serviceResponse.id}</td>
        <td data-name="productEntityImageUrl" data-value="${serviceResponse.productEntity.imageUrl}">
            <img alt="Spa" src="${serviceResponse.productEntity.imageUrl}"
                 style="width:60px; height:60px; object-fit:cover; border-radius:4px;">
        </td>
        <td data-name="productEntityName">${serviceResponse.productEntity.name}</td>
        <td data-name="productEntityDescription" class="truncate">${serviceResponse.productEntity.description}</td>
        <td data-name="productEntityPrice" data-value="${serviceResponse.productEntity.price}">${FormatUtils.formatPrice(serviceResponse.productEntity.price)}</td>
        <td data-name="departmentEntityId">${serviceResponse.departmentEntity.name}</td>
        <td>${FormatUtils.formatNumber(serviceResponse.productEntity.averageRating, 1)} 
                                <svg class="icon-24 text-warning ps-2 pb-1" width="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                                    <path fill-rule="evenodd" clip-rule="evenodd" d="M13.1043 4.17701L14.9317 7.82776C15.1108 8.18616 15.4565 8.43467 15.8573 8.49218L19.9453 9.08062C20.9554 9.22644 21.3573 10.4505 20.6263 11.1519L17.6702 13.9924C17.3797 14.2718 17.2474 14.6733 17.3162 15.0676L18.0138 19.0778C18.1856 20.0698 17.1298 20.8267 16.227 20.3574L12.5732 18.4627C12.215 18.2768 11.786 18.2768 11.4268 18.4627L7.773 20.3574C6.87023 20.8267 5.81439 20.0698 5.98724 19.0778L6.68385 15.0676C6.75257 14.6733 6.62033 14.2718 6.32982 13.9924L3.37368 11.1519C2.64272 10.4505 3.04464 9.22644 4.05466 9.08062L8.14265 8.49218C8.54354 8.43467 8.89028 8.18616 9.06937 7.82776L10.8957 4.17701C11.3477 3.27433 12.6523 3.27433 13.1043 4.17701Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"></path>
                                </svg>
                            </td>
        <td>${serviceResponse.productEntity.reviewCount}</td>
        <td>
        <a aria-controls="editForm" class="edit-button d-inline-block"
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