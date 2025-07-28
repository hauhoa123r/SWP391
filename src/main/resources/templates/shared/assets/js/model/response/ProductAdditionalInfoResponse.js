export class ProductAdditionalInfoResponse {
    constructor(id, key, value) {
        this.id = id;
        this.key = key;
        this.value = value;
    }

    static fromJson(json) {
        return new ProductAdditionalInfoResponse(
            json.id,
            json.key,
            json.value
        );
    }

    static fromJsonArray(jsonArray) {
        return jsonArray.map(json => ProductAdditionalInfoResponse.fromJson(json));
    }
}

