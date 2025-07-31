export class DoctorDTO {
    constructor(staffEntityFullName, staffEntityDepartmentEntityId, minStarRating, sortFieldName, sortDirection) {
        this.staffEntityFullName = staffEntityFullName;
        this.staffEntityDepartmentEntityId = staffEntityDepartmentEntityId;
        this.minStarRating = minStarRating;
        this.sortFieldName = sortFieldName;
        this.sortDirection = sortDirection;
    }

    static fromObject(obj) {
        const doctorDTO = new DoctorDTO();
        if ("staffEntityFullName" in obj) {
            doctorDTO.staffEntityFullName = obj.staffEntityFullName;
        }
        if ("staffEntityDepartmentEntityId" in obj) {
            doctorDTO.staffEntityDepartmentEntityId = obj.staffEntityDepartmentEntityId;
        }
        if ("minStarRating" in obj) {
            doctorDTO.minStarRating = obj.minStarRating;
        }
        if ("sortFieldName" in obj) {
            doctorDTO.sortFieldName = obj.sortFieldName;
        }
        if ("sortDirection" in obj) {
            doctorDTO.sortDirection = obj.sortDirection;
        }
    }
}