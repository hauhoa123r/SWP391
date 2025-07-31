export class StaffEducationResponse {
    constructor(id, year, degree, institute, result) {
        this.id = id;
        this.year = year;
        this.degree = degree;
        this.institute = institute;
        this.result = result;
    }

    static fromJson(json) {
        return new StaffEducationResponse(
            json.id,
            json.year,
            json.degree,
            json.institute,
            json.result
        );
    }

    static fromJsonArray(jsonArray) {
        return jsonArray.map(json => StaffEducationResponse.fromJson(json));
    }
}
