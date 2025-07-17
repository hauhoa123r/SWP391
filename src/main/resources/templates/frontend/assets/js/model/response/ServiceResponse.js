import {BaseResponse} from "/frontend/assets/js/model/response/BaseResponse.js";
import {ProductResponse} from "/frontend/assets/js/model/response/ProductResponse.js";

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

    toHtml() {
        return `
        <div class="col-sm-4 mb-4">
            <div
                    class="form-check form-check-inline m-0 p-0 position-relative d-block box-checked h-100 service">
                <input class="form-check-input" id="service-${this.id}"
                       name="radios" type="radio" value="${this.id}">
                <label
                        class="form-check-label d-inline-block overflow-hidden w-100 h-100"
                        for="service-${this.id}">
                        <span class="d-block p-4 text-center position-relative">
                            <span class="d-block mb-3 position-relative">
                                <img alt="img"
                                     class="rounded-circle object-cover"
                                     height="70"
                                     src="${this.productEntity.imageUrl}"
                                     width="70">
                            </span>
                            <span
                                    class="d-block h6 fw-500 mt-3 mb-1 service-name">${this.productEntity.name}</span>
                            <span class="text-body service-price">${this.productEntity.price}$</span>
                        </span>
                </label>
            </div>
        </div>
        `;
    }
}
