export class ProductAdditionalInfoResponse {
    constructor(id, name, value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    static fromJson(json) {
        return new ProductAdditionalInfoResponse(
                json.id,
                json.name,
                json.value
        );
    }

    static fromJsonArray(jsonArray) {
        return jsonArray.map(json => ProductAdditionalInfoResponse.fromJson(json));
    }
}

