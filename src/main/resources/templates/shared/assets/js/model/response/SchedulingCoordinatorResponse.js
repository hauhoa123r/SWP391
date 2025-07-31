import {StaffResponse} from "/templates/shared/assets/js/model/response/StaffResponse.js";

export class SchedulingCoordinatorResponse {
    /**
     * Constructor for SchedulingCoordinatorResponse
     * @param id
     * @param {StaffResponse} StaffEntity
     */
    constructor(id, StaffEntity) {
        this.id = id;
        this.staffEntity = StaffEntity;
    }

    static fromJson(json) {
        const schedulingCoordinatorEntity = new SchedulingCoordinatorResponse();
        if (json.staffEntity) {
            schedulingCoordinatorEntity.staffEntity = StaffResponse.fromJson(json.staffEntity);
        }
        if (json.id) {
            schedulingCoordinatorEntity.id = json.id;
        }
        return schedulingCoordinatorEntity;
    }

    static fromJsonArray(jsonArray) {
        return jsonArray.map(json => this.fromJson(json));
    }
}