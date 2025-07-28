export class StaffSpecialityResponse {
    constructor(id, title, description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    static fromJson(json) {
        return new StaffSpecialityResponse(
            json.id,
            json.title,
            json.description
        );
    }

    static fromJsonArray(jsonArray) {
        return jsonArray.map(json => StaffSpecialityResponse.fromJson(json));
    }
}
