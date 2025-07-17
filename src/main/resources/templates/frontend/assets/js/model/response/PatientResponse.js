import {BaseResponse} from "/frontend/assets/js/model/response/BaseResponse.js";

export class PatientResponse extends BaseResponse {
    constructor(id, phoneNumber, email, fullName, avatarUrl, address, birthdate, familyRelationship, gender, bloodType) {
        super();
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.fullName = fullName;
        this.avatarUrl = avatarUrl;
        this.address = address;
        this.birthdate = birthdate;
        this.familyRelationship = familyRelationship;
        this.gender = gender;
        this.bloodType = bloodType;
    }

    static fromJson(json) {
        return new PatientResponse(
            json.id,
            json.phoneNumber,
            json.email,
            json.fullName,
            json.avatarUrl,
            json.address,
            json.birthdate,
            json.familyRelationship,
            json.gender,
            json.bloodType
        );
    }

    static fromJsonArray(jsonArray) {
        return jsonArray.map(json => PatientResponse.fromJson(json));
    }

    toHtml() {
        return `
        <div class="col-sm-k mt-4">
            <div class="form-check form-check-inline m-0 p-0 position-relative d-block box-checked patient">
                <input type="radio" name="patient" class="form-check-input" id="patient-${this.id}" value="${this.id}">
                <label class="form-check-label d-inline-block overflow-hidden w-100" for="patient-${this.id}">
                    <span class="d-block appointment-patient-box p-4 position-relative">
                        <div class="row">
                            <div class="col-md-4 text-center">
                                <span class="d-block mb-3 position-relative">
                                    <img alt="Ảnh bệnh nhân" src="${this.avatarUrl}" height="100" width="100" class="rounded-circle object-cover p-1 bg-white">
                                </span>
                                <div class="mb-1">
                                    <span class="h5 patient-name">${this.fullName}</span>
                                    <span class="text-body fst-italic">(${this.familyRelationship})</span>
                                </div>
                                <span class="bg-info px-3 py-2 d-inline-block rounded-pill text-white mb-2">${this.bloodType}</span>
                                <span class="d-block text-body mb-2">${this.gender}</span>
                            </div>
                            <div class="col-md-8">
                                <div class="d-block patient-info mt-md-0 mt-3">
                                    <div class="row mb-2">
                                        <div class="col-sm-4">
                                            <span class="fw-medium text-secondary">Email:</span>
                                        </div>
                                        <div class="col-sm-8">
                                            <span class="text-body patient-email">${this.email}</span>
                                        </div>
                                    </div>
                                    <div class="row mb-2">
                                        <div class="col-sm-4">
                                            <span class="fw-medium text-secondary">Số điện thoại:</span>
                                        </div>
                                        <div class="col-sm-8">
                                            <span class="text-body patient-phone">${this.phoneNumber}</span>
                                        </div>
                                    </div>
                                    <div class="row mb-2">
                                        <div class="col-sm-4">
                                            <span class="fw-medium text-secondary">Địa chỉ:</span>
                                        </div>
                                        <div class="col-sm-8">
                                            <span class="text-body">${this.address}</span>
                                        </div>
                                    </div>
                                    <div class="row mb-2">
                                        <div class="col-sm-4">
                                            <span class="fw-medium text-secondary">Ngày sinh:</span>
                                        </div>
                                        <div class="col-sm-8">
                                            <span class="text-body">${this.birthdate}</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </span>
                </label>
            </div>
        </div>
        `;
    }
}
