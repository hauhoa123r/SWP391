import {DoctorResponse} from "/templates/shared/assets/js/model/response/DoctorResponse.js";
import {PatientResponse} from "/templates/shared/assets/js/model/response/PatientResponse.js";
import {
    SchedulingCoordinatorResponse
} from "/templates/shared/assets/js/model/response/SchedulingCoordinatorResponse.js";
import {ServiceResponse} from "/templates/shared/assets/js/model/response/ServiceResponse.js";
import {FormatUtils} from "/templates/shared/assets/js/utils/format-utils.js";

export class AppointmentResponse {
    /**
     * Constructor for AppointmentResponse
     * @param id
     * @param {DoctorResponse} doctorEntity
     * @param {PatientResponse} patientEntity
     * @param {ServiceResponse} serviceEntity
     * @param startTime
     * @param durationMinutes
     * @param appointmentStatus
     * @param {SchedulingCoordinatorResponse} schedulingCoordinatorEntity
     */
    constructor(id, doctorEntity, patientEntity, serviceEntity, startTime, durationMinutes, appointmentStatus, schedulingCoordinatorEntity) {
        this.id = id;
        this.doctorEntity = doctorEntity;
        this.patientEntity = patientEntity;
        this.serviceEntity = serviceEntity;
        this.startTime = startTime;
        this.durationMinutes = durationMinutes;
        this.appointmentStatus = appointmentStatus;
        this.schedulingCoordinatorEntity = schedulingCoordinatorEntity;
    }

    static fromJson(json) {
        const appointmentResponse = new AppointmentResponse();
        if (json.id) {
            appointmentResponse.id = json.id;
        }
        if (json.doctorEntity) {
            appointmentResponse.doctorEntity = DoctorResponse.fromJson(json.doctorEntity);
        }
        if (json.patientEntity) {
            appointmentResponse.patientEntity = PatientResponse.fromJson(json.patientEntity);
        }
        if (json.serviceEntity) {
            appointmentResponse.serviceEntity = ServiceResponse.fromJson(json.serviceEntity);
        }
        if (json.startTime) {
            appointmentResponse.startTime = json.startTime;
        }
        if (json.durationMinutes) {
            appointmentResponse.durationMinutes = json.durationMinutes + " phút";
        } else {
            appointmentResponse.durationMinutes = "Chưa khám";
        }
        if (json.appointmentStatus) {
            appointmentResponse.appointmentStatus = json.appointmentStatus;
        }
        if (json.schedulingCoordinatorEntity) {
            appointmentResponse.schedulingCoordinatorEntity = SchedulingCoordinatorResponse.fromJson(json.schedulingCoordinatorEntity);
        }
        return appointmentResponse;
    }

    static fromJsonArray(jsonArray) {
        return jsonArray.map(json => AppointmentResponse.fromJson(json));
    }

    setRenderStrategy(strategy) {
        this.renderStrategy = strategy;
        return this;
    }

    render(...args) {
        if (this.renderStrategy) {
            return this.renderStrategy(this, ...args);
        }
        throw new Error("Render strategy is not set for AppointmentResponse");
    }
}

/**
 * Render an appointment response for admin
 * @param {AppointmentResponse} appointmentResponse - The appointment response object to render
 * @returns {string}
 */
export function renderAppointmentResponseAdmin(appointmentResponse) {
    return `
        <tr data-item="list">
            <input type="hidden" name="id" value="${appointmentResponse.id}">
            <th scope="row">${appointmentResponse.id}</th>
            <td>
                <div class="d-flex align-items-center gap-3">
                    <img alt="icon"
                         class="img-fluid flex-shrink-0 icon-40 object-fit-cover"
                         src="${appointmentResponse.patientEntity.avatarUrl}">
                    <h5 class="mb-0">${appointmentResponse.patientEntity.fullName}</h5>
                </div>
            </td>
            <td>${appointmentResponse.patientEntity.email}</td>
            <td>${appointmentResponse.serviceEntity.productEntity.name}</td>
            <td>${appointmentResponse.doctorEntity.staffEntity.fullName}</td>
            <td>${appointmentResponse?.schedulingCoordinatorEntity?.staffEntity?.fullName || "Không xác định"}</td>
            <td>${FormatUtils.formatDate(appointmentResponse.startTime)}</td>
            <td>${appointmentResponse.durationMinutes}</td>
            <td>${appointmentResponse.appointmentStatus}</td>
        </tr>
    `;
}