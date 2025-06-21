export class StaffSkillResponse {
    constructor(id, name, proficiencyPercentage) {
        this.id = id;
        this.name = name;
        this.proficiencyPercentage = proficiencyPercentage;
    }

    static fromJson(json) {
        return new StaffSkillResponse(
            json.id,
            json.name,
            json.proficiencyPercentage
        );
    }

    static fromJsonArray(jsonArray) {
        return jsonArray.map(json => StaffSkillResponse.fromJson(json));
    }
}
