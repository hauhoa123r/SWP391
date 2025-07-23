export class CategoryResponse {
    constructor(id, name) {
        this.id = id;
        this.name = name;
    }

    static fromJson(json) {
        return new CategoryResponse(
            json.id,
            json.name
        );
    }

    static fromJsonArray(jsonArray) {
        return jsonArray.map(json => CategoryResponse.fromJson(json));
    }
}

