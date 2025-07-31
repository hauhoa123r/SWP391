export class ServiceFeatureResponse {
    constructor(id, name, description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    static fromJson(json) {
        return new ServiceFeatureResponse(
                json.id,
                json.name,
                json.description
        );
    }

    static fromJsonArray(jsonArray) {
        return jsonArray.map(json => ServiceFeatureResponse.fromJson(json));
    }
}