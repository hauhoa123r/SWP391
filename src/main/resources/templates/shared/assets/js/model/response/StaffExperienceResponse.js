export class StaffExperienceResponse {
    constructor(id, year, department, position, hospital, result) {
        this.id = id;
        this.year = year;
        this.department = department;
        this.position = position;
        this.hospital = hospital;
        this.result = result;
    }

    static fromJson(json) {
        return new StaffExperienceResponse(
            json.id,
            json.year,
            json.department,
            json.position,
            json.hospital,
            json.result
        );
    }

    static fromJsonArray(jsonArray) {
        return jsonArray.map(json => StaffExperienceResponse.fromJson(json));
    }
}
