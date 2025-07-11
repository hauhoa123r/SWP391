import {AppointmentDTO} from "/templates/frontend/assets/js/model/dto/AppointmentDTO.js";
import {
    DepartmentResponse, renderDepartmentResponseForBooking
} from "/templates/frontend/assets/js/model/response/DepartmentResponse.js";
import {
    DoctorResponse, renderDoctorResponseForBooking
} from "/templates/frontend/assets/js/model/response/DoctorResponse.js";
import {
    HospitalResponse, renderHospitalResponseForBooking
} from "/templates/frontend/assets/js/model/response/HospitalResponse.js";
import {
    PatientResponse, renderPatientResponseForBooking
} from "/templates/frontend/assets/js/model/response/PatientResponse.js";
import {
    renderServiceResponseForBooking, ServiceResponse
} from "/templates/frontend/assets/js/model/response/ServiceResponse.js";
import {renderTimeResponseForBooking, TimeResponse} from "/templates/frontend/assets/js/model/response/TimeResponse.js";
import toast from "/templates/frontend/assets/js/plugins/toast.js";
import {FetchingUtils} from "/templates/frontend/assets/js/utils/fetching-utils.js";
import {Pagination} from "/templates/frontend/assets/js/utils/pagination.js";

document.addEventListener("DOMContentLoaded", async () => {
    const bookingManager = new BookingManager();
    await bookingManager.init();
});

const defaultTabConfigs = {
    rootUrl: "",
    objectJsonName: "",
    object: null,
    renderStrategy: null,
    prefix: "",
    urlFilter: "",
    prevTab: "",
    nextTab: "",
    currentPage: 0,
    customSearch: null,
    customUrlBuilder: null,
    customSelect: null
};

class BookingManager {
    constructor() {
        this.isProcessing = false;
        this.isLoading = false;
        this.tabConfigs = {
            hospital: {
                ...defaultTabConfigs,
                rootUrl: "/api/hospital",
                objectJsonName: "hospitals",
                object: HospitalResponse,
                renderStrategy: renderHospitalResponseForBooking,
                prefix: "hospital",
                nextTab: "department"
            }, department: {
                ...defaultTabConfigs,
                rootUrl: "/api/department",
                objectJsonName: "departments",
                object: DepartmentResponse,
                renderStrategy: renderDepartmentResponseForBooking,
                prefix: "department",
                prevTab: "hospital",
                nextTab: "doctor"
            }, doctor: {
                ...defaultTabConfigs,
                rootUrl: "/api/doctor",
                objectJsonName: "doctors",
                object: DoctorResponse,
                renderStrategy: renderDoctorResponseForBooking,
                prefix: "doctor",
                prevTab: "department",
                nextTab: "service"
            }, service: {
                ...defaultTabConfigs,
                rootUrl: "/api/service",
                objectJsonName: "services",
                object: ServiceResponse,
                renderStrategy: renderServiceResponseForBooking,
                prefix: "service",
                prevTab: "doctor",
                nextTab: "patient"
            }, patient: {
                ...defaultTabConfigs,
                rootUrl: "/api/patient",
                objectJsonName: "patients",
                object: PatientResponse,
                renderStrategy: renderPatientResponseForBooking,
                prefix: "patient",
                prevTab: "service",
                nextTab: "dateTime"
            }, dateTime: {
                ...defaultTabConfigs,
                rootUrl: "/api/schedule",
                objectJsonName: "availableTimes",
                object: TimeResponse,
                renderStrategy: renderTimeResponseForBooking,
                prefix: "date-time",
                prevTab: "patient",
                nextTab: "confirmation",
                customUrlBuilder: function (manager) {
                    let apiUrl = `/api/schedule/staff/${manager.selectedIds.doctor}/patient/${manager.selectedIds.patient}`;
                    if (!manager.searchKeywords.dateTime) {
                        manager.searchKeywords.dateTime = new Date().toISOString().split("T")[0];
                    }
                    apiUrl += `/date/${encodeURIComponent(manager.searchKeywords.dateTime)}`;

                    return apiUrl;
                },
                customSearch: function (manager) {
                    document.querySelector("#date-input").addEventListener("change", function (event) {
                        const selectedDate = event.target.value;
                        if (selectedDate) {
                            manager.searchKeywords.dateTime = selectedDate;
                            manager.loadData(0);
                        } else {
                            toast.warning("Please select a date first.", {
                                position: "top-right", icon: true, duration: 3000, progress: true
                            });
                        }
                    });
                },
                customSelect: function (id) {
                    return document.querySelector(`input[value="${id}"]`);
                }
            }, confirmation: {
                ...defaultTabConfigs, prevTab: "patient"
            }
        };
        this.currentTab = null;
        this.searchKeywords = {};
        this.selectedIds = {};
        this.bookingSummary = {
            hospital: {
                name: null, address: null
            }, patient: {
                name: null, phone: null, email: null
            }, doctor: {
                name: null
            }, dateTime: {
                date: null, time: null
            }, service: {
                name: null, price: null
            }
        };
        Object.keys(this.tabConfigs).forEach(tabKey => {
            this.searchKeywords[tabKey] = null;
            this.selectedIds[tabKey] = null;
        });

    }

    async init() {
        await this.nextTab();
        await this.setupEventListeners();
    }

    async setupEventListeners() {
        Object.keys(this.tabConfigs).forEach(tabName => {
            const config = this.tabConfigs[tabName];
            if (!config.customSearch) {
                const searchButton = document.querySelector(`#${config.prefix}-search-button`);
                const searchInput = document.querySelector(`#${config.prefix}-search-input`);
                if (searchInput && searchButton) {
                    searchInput.addEventListener("keyup", (e) => {
                        if (e.key === "Enter") {
                            searchButton.click();
                        }
                    });
                    searchButton.addEventListener("click", async () => {
                        const searchValue = searchInput.value.trim();
                        if (searchValue !== this.searchKeywords[tabName]) {
                            this.searchKeywords[tabName] = searchValue || null; // Update the current keyword
                            await this.loadData(0);
                        }
                    });
                }
            } else {
                config.customSearch(this);
            }
        });

        document.querySelector("#hospital-next-button").addEventListener("click", async (event) => {
            const inputHospitalElement = document.querySelector("#hospital-list input:checked");
            if (inputHospitalElement) {
                const selectedHospitalElement = inputHospitalElement.closest(".hospital");
                this.bookingSummary.hospital = {
                    name: selectedHospitalElement.querySelector(".hospital-name").textContent.trim(),
                    address: selectedHospitalElement.querySelector(".hospital-address").textContent.trim()
                };
                if (this.selectedIds.hospital !== inputHospitalElement.value) {
                    this.selectedIds.department = null; // Reset doctor selection if a new hospital is selected
                }
                this.selectedIds.hospital = inputHospitalElement.value;
                this.tabConfigs.department.urlFilter = `/hospital/${this.selectedIds.hospital}`;
                await this.nextTab();
            } else {
                toast.warning("Please select a hospital first.", {
                    position: "top-right", icon: true, duration: 3000
                });
                event.stopImmediatePropagation();
            }
            this.isLoading = true;
        });

        document.querySelector("#department-next-button").addEventListener("click", async (event) => {
            const inputDepartmentElement = document.querySelector("#department-list input:checked");
            if (inputDepartmentElement) {
                if (this.selectedIds.department !== inputDepartmentElement.value) {
                    this.selectedIds.doctor = null; // Reset doctor selection if a new department is selected
                }

                this.selectedIds.department = inputDepartmentElement.value;
                this.tabConfigs.doctor.urlFilter = `/hospital/${this.selectedIds.hospital}/department/${this.selectedIds.department}`;
                await this.nextTab();
            } else {
                toast.warning("Please select a department first.", {
                    position: "top-right", icon: true, duration: 3000
                });
                event.stopImmediatePropagation();
            }
        });

        document.querySelector("#department-previous-button").addEventListener("click", async () => {
            await this.previousTab();
        });

        document.querySelector("#doctor-next-button").addEventListener("click", async (event) => {
            const inputDoctorElement = document.querySelector("#doctor-list input:checked");
            if (inputDoctorElement) {
                const selectedDoctorElement = inputDoctorElement.closest(".doctor");
                this.bookingSummary.doctor = {
                    name: selectedDoctorElement.querySelector(".doctor-name").textContent.trim()
                };
                if (this.selectedIds.doctor !== inputDoctorElement.value) {
                    this.selectedIds.service = null; // Reset service selection if a new doctor is selected
                    this.selectedIds.dateTime = null;
                }
                this.selectedIds.doctor = inputDoctorElement.value;
                this.tabConfigs.service.urlFilter = `/department/${this.selectedIds.department}`;
                await this.nextTab();
            } else {
                toast.warning("Please select a doctor first.", {
                    position: "top-right", icon: true, duration: 3000
                });
                event.stopImmediatePropagation();
            }
        });

        document.querySelector("#doctor-previous-button").addEventListener("click", async () => {
            await this.previousTab();
        });

        document.querySelector("#service-next-button").addEventListener("click", async (event) => {
            const inputServiceElement = document.querySelector("#service-list input:checked");
            if (inputServiceElement) {
                const selectedServiceElement = inputServiceElement.closest(".service");
                this.bookingSummary.service = {
                    name: selectedServiceElement.querySelector(".service-name").textContent.trim(),
                    price: selectedServiceElement.querySelector(".service-price").textContent.trim()
                };
                this.selectedIds.service = inputServiceElement.value;
                const userId = document.querySelector("#user-id").value;
                this.tabConfigs.patient.urlFilter = `/user/${userId}`;
                await this.nextTab();
            } else {
                toast.warning("Please select a service first.", {
                    position: "top-right", icon: true, duration: 3000
                });
                event.stopImmediatePropagation();
            }
        });

        document.querySelector("#service-previous-button").addEventListener("click", async () => {
            await this.previousTab();
        });

        document.querySelector("#patient-next-button").addEventListener("click", async () => {
            const inputPatientElement = document.querySelector("#patient-list input:checked");
            if (inputPatientElement) {
                const selectedPatientElement = inputPatientElement.closest(".patient");
                this.bookingSummary.patient = {
                    name: selectedPatientElement.querySelector(".patient-name").textContent.trim(),
                    phone: selectedPatientElement.querySelector(".patient-phone").textContent.trim(),
                    email: selectedPatientElement.querySelector(".patient-email").textContent.trim()
                };
                this.selectedIds.patient = inputPatientElement.value;
                await this.nextTab();
            } else {
                toast.warning("Please select a patient first.", {
                    position: "top-right", icon: true, duration: 3000
                });
            }
        });

        document.querySelector("#patient-previous-button").addEventListener("click", async () => {
            await this.previousTab();
            const flatpickrInstance = document.querySelector(".inline_flatpickr")._flatpickr;
            const selectedDate = new Date(this.searchKeywords.dateTime || new Date().toISOString().split("T")[0]);
            flatpickrInstance.setDate(selectedDate, true);
        });

        document.querySelector("#date-time-next-button").addEventListener("click", (event) => {
            const inputTimeElement = document.querySelector("#date-time-list input:checked");
            if (inputTimeElement) {

                const date = new Date(inputTimeElement.value);
                this.bookingSummary.dateTime = {
                    date: date.toLocaleDateString("en-US", {
                        month: "long", day: "numeric", year: "numeric"
                    }), time: date.toLocaleTimeString("en-US", {
                        hour: "2-digit", minute: "2-digit", hour12: true
                    })
                };
                this.selectedIds.dateTime = inputTimeElement.value;
                this.showConfirmation();
            } else {
                toast.warning("Please select a time slot first.", {
                    position: "top-right", icon: true, duration: 3000
                });
                event.stopImmediatePropagation();
            }
        });

        document.querySelector("#date-time-previous-button").addEventListener("click", async () => {
            await this.previousTab();
        });

        document.querySelector("#confirm-button").addEventListener("click", async (event) => {
            if (this.isProcessing) {
                return;
            } else {
                event.stopImmediatePropagation();
            }

            const appointment = new AppointmentDTO(
                    this.selectedIds.doctor,
                    this.selectedIds.patient,
                    this.selectedIds.service,
                    this.selectedIds.dateTime
            );

            const data = await FetchingUtils.fetch("/api/patient/appointment", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(appointment)
            }, "text");

            if (data !== null) {
                toast.success("Booking confirmed successfully!", {
                    position: "top-right", icon: true, duration: 3000
                });
                this.isProcessing = true;
                event.target.click();
            } else {
                toast.danger(data, {
                    position: "top-right", icon: true, duration: 3000
                });
            }
        });

        document.querySelector("#confirmation-previous-button").addEventListener("click", async () => {
            this.currentTab = "confirmation";
            await this.previousTab();
        });
    }

    async previousTab() {
        if (!this.isLoading) {
            this.isLoading = true;
            this.changeButtonStatus();
            this.currentTab = this.tabConfigs[this.currentTab].prevTab || "hospital";
            await this.loadData();
        }
    }

    async nextTab() {
        if (this.isLoading) return;

        this.isLoading = true;

        this.changeButtonStatus();
        if (this.currentTab) {
            this.currentTab = this.tabConfigs[this.currentTab].nextTab || "confirmation";
        } else {
            this.currentTab = "hospital"; // Reset to hospital if no next tab is defined
        }
        await this.loadData();
    }

    async loadData(pageIndex = this.tabConfigs[this.currentTab].currentPage) {
        const config = this.tabConfigs[this.currentTab];
        let apiUrl;
        if (config.customUrlBuilder) {
            apiUrl = config.customUrlBuilder(this);
        } else {
            const searchKeyword = this.searchKeywords[this.currentTab];
            this.tabConfigs[this.currentTab].currentPage = pageIndex;

            apiUrl = `${config.rootUrl}/page/${pageIndex}${config.urlFilter}`;
            if (searchKeyword) {
                apiUrl += `/search/${encodeURIComponent(searchKeyword)}`;
            }
        }

        const data = await FetchingUtils.fetch(apiUrl);
        if (!data) return;

        if (config.objectJsonName in data) {
            this.renderListObject(config, data[config.objectJsonName]);
        }

        if (this.selectedIds[this.currentTab] !== undefined) {
            this.selectSelectedObject(config);
        }

        if ("currentPage" in data && "totalPages" in data) {
            this.renderPagination(config, data);
        }

        setTimeout(() => {
            this.isLoading = false;
            this.changeButtonStatus();
        }, 1000);
    }

    renderListObject(config, objects) {
        const objectList = config.object.fromJsonArray(objects);
        const htmlContent = objectList.map(item => item.setRenderStrategy(config.renderStrategy).toHtml()).join("");
        const objectListElement = document.querySelector(`#${config.prefix}-list`);
        if (!objectListElement) return;
        objectListElement.innerHTML = htmlContent;
    }

    selectSelectedObject(config) {
        let objectInput;
        const id = this.selectedIds[this.currentTab];
        if (!config.customSelect) {
            objectInput = document.querySelector(`#${config.prefix}-${id}`);
        } else {
            objectInput = config.customSelect(id);
        }
        if (objectInput) {
            objectInput.checked = true;
        }
    }

    renderPagination(config, data) {
        const pagination = new Pagination(data.currentPage, data.totalPages);
        const paginationHtml = pagination.toHtml();
        const paginationElement = document.querySelector(`#${config.prefix}-pagination`);
        if (paginationElement) {
            paginationElement.innerHTML = paginationHtml;
        }
        pagination.setEvent((pageIndex) => this.loadData(pageIndex));
    }

    changeButtonStatus() {
        const buttons = document.querySelectorAll("[id$='-next-button'], [id$='-previous-button']");
        buttons.forEach(button => {
            button.disabled = this.isLoading;
            if (this.isLoading) {
                button.classList.add("disabled");
            } else {
                button.classList.remove("disabled");
            }
        });
    }

    showConfirmation() {
        const confirmationElement = document.querySelector("#confirmation");

        const confirmationHTML = `
            <div class="col-sm-6">
                <h6 class="text-secondary mb-3 fw-500 text-uppercase">
                Thông tin bệnh viện
                </h6>
                <div class="p-4 bg-primary-subtle">
                    <h6 class="mb-2">${this.bookingSummary.hospital.name}</h6>
                    <p class="m-0 text-body">${this.bookingSummary.hospital.address}</p>
                </div>
                <h6 class="text-secondary mt-5 mb-3 fw-500 text-uppercase">Thông tin bệnh nhân</h6>
                <div class="p-4 bg-primary-subtle">
                    <div class="table-responsive">
                        <table class="table mb-0">
                            <tbody>
                            <tr>
                                <td class="p-0 border-0"><h6 class="mb-2">Tên:</h6></td>
                                <td class="p-0 border-0"><p class="mb-2 text-end">${this.bookingSummary.patient.name}</p></td>
                            </tr>
                            <tr>
                                <td class="p-0 border-0"><h6 class="mb-2">Số điện thoại:</h6></td>
                                <td class="p-0 border-0"><p class="mb-2 text-end">${this.bookingSummary.patient.phone}</p></td>
                            </tr>
                            <tr>
                                <td class="p-0 border-0"><h6 class="mb-0">Email:</h6></td>
                                <td class="p-0 border-0"><p class="mb-0 text-end">${this.bookingSummary.patient.email}</p></td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
            <div class="col-sm-6 mt-sm-0 mt-5">
                <h6 class="text-secondary mb-3 fw-500 text-uppercase">
                Tóm tắt cuộc hẹn
                </h6>
                <div class="p-4 border">
                    <div class="table-responsive">
                        <table class="table mb-0">
                            <tbody>
                            <tr>
                                <td class="p-0 border-0"><p class="mb-2">Bác sĩ:</p></td>
                                <td class="p-0 border-0"><h6 class="mb-2 text-end">${this.bookingSummary.doctor.name}</h6></td>
                            </tr>
                            <tr>
                                <td class="p-0 border-0"><p class="mb-2">Ngày:</p></td>
                                <td class="p-0 border-0"><h6 class="mb-2 text-end">${this.bookingSummary.dateTime.date}</h6></td>
                            </tr>
                            <tr>
                                <td class="p-0 border-0"><p class="mb-0">Giờ:</p></td>
                                <td class="p-0 border-0"><h6 class="mb-0 text-end">${this.bookingSummary.dateTime.time}</h6></td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                    <div class="p-4 bg-primary-subtle mt-4">
                        <h6 class="mb-2">Dịch vụ</h6>
                        <div class="d-flex align-items-center justify-content-between flex-wrap gap-3">
                            <p class="m-0 text-body">${this.bookingSummary.service.name}</p>
                            <h6 class="m-0">${this.bookingSummary.service.price}</h6>
                        </div>
                    </div>
                    <div class="d-flex align-items-center justify-content-between flex-wrap gap-3 mt-4">
                        <h5 class="m-0">Tổng giá</h5>
                        <p class="m-0 text-primary">${this.bookingSummary.service.price}</p>
                    </div>
                </div>
            </div>
          `;
        if (confirmationElement) {
            confirmationElement.innerHTML = confirmationHTML;
        }
    }
}