import {CategoryResponse} from "/templates/frontend/assets/js/model/response/CategoryResponse.js";
import {
    ProductAdditionalInfoResponse
} from "/templates/frontend/assets/js/model/response/ProductAdditionalInfoResponse.js";

export class ProductResponse {
    constructor(id, name, description, imageUrl, price, categoryEntities, productAdditionalInfoEntities, reviewCount, averageRating) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.categoryEntities = categoryEntities;
        this.productAdditionalInfoEntities = productAdditionalInfoEntities;
        this.reviewCount = reviewCount;
        this.averageRating = averageRating;
    }

    static fromJson(json) {
        const productResponse = new ProductResponse(
            json.id,
            json.name,
            json.description,
            json.imageUrl,
            json.price,
            json.categoryEntities,
            json.productAdditionalInfoEntities,
            json.reviewCount,
            json.averageRating
        );

        if (json.categoryEntities) {
            productResponse.categoryEntities = CategoryResponse.fromJsonArray(json.categoryEntities);
        }
        if (json.productAdditionalInfoEntities) {
            productResponse.productAdditionalInfoEntities = ProductAdditionalInfoResponse.fromJsonArray(json.productAdditionalInfoEntities);
        }
        return productResponse;
    }

    static fromJsonArray(jsonArray) {
        return jsonArray.map(json => ProductResponse.fromJson(json));
    }
}
