export class HospitalDTO {
    constructor(name, address, sortFieldName, sortDirection) {
        this.name = name;
        this.address = address;
        this.sortFieldName = sortFieldName;
        this.sortDirection = sortDirection;
    }
}