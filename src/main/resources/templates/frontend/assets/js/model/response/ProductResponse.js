export class ProductResponse {
    constructor(name, price, imageUrl, duration) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.duration = duration;
    }

    static fromJson(json) {
        return new ProductResponse(
            json.name,
            json.price,
            json.imageUrl,
            json.duration
        );
    }

    static fromJsonArray(jsonArray) {
        return jsonArray.map(json => ProductResponse.fromJson(json));
    }
}
