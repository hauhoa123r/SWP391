export class StaffQualificationResponse {
    constructor(id, title, description, issueDate, expirationDate, url) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.issueDate = issueDate;
        this.expirationDate = expirationDate;
        this.url = url;
    }

    static fromJson(json) {
        return new StaffQualificationResponse(
            json.id,
            json.title,
            json.description,
            json.issueDate,
            json.expirationDate,
            json.url
        );
    }

    static fromJsonArray(jsonArray) {
        return jsonArray.map(json => StaffQualificationResponse.fromJson(json));
    }
}
