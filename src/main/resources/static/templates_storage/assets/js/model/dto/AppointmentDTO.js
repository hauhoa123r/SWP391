export class AppointmentDTO {
    constructor(doctorEntityId, patientEntityId, serviceEntityId, startTime) {
        this.doctorEntityId = doctorEntityId;
        this.patientEntityId = patientEntityId;
        this.serviceEntityId = serviceEntityId;
        this.startTime = startTime;
    }
}