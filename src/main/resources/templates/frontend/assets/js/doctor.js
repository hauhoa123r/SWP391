import {DoctorDTO} from "/templates/frontend/assets/js/model/dto/DoctorDTO.js";
import {
    DepartmentResponse, renderDepartmentResponseForSelect
} from "/templates/frontend/assets/js/model/response/DepartmentResponse.js";
import {
    DoctorResponse, renderDoctorResponseForList
} from "/templates/frontend/assets/js/model/response/DoctorResponse.js";
import {FetchingUtils} from "/templates/frontend/assets/js/utils/fetching-utils.js";
import {FormDataUtils} from "/templates/frontend/assets/js/utils/form-data.js";
import {Pagination} from "/templates/frontend/assets/js/utils/pagination.js";
import {SearchParamsUtils} from "/templates/frontend/assets/js/utils/search-params-utils.js";

const $ = document.querySelector.bind(document);
const $$ = document.querySelectorAll.bind(document);

document.addEventListener("DOMContentLoaded", async () => {
    const renderDoctor = new RenderDoctor();
    await renderDoctor.renderDepartmentOption();
    await renderDoctor.renderDoctorList();
    renderDoctor.setupFilterFormEvents();
});

class RenderDoctor {
    constructor() {
        this.doctorDTO = new DoctorDTO();
    }

    async renderDepartmentOption(departmentId) {
        const departmentSelectElement = $("#department-select");
        if (!departmentSelectElement) return;
        const departmentData = await FetchingUtils.fetch("/api/department");
        const defaultOption = `
        <option value="">Tất cả các khoa</option>
    `;
        if (departmentData) {
            departmentSelectElement.innerHTML = defaultOption + DepartmentResponse.fromJsonArray(departmentData).map(department => department.setRenderStrategy(renderDepartmentResponseForSelect).toHtml(departmentId)).join("");
        }
    }

    async renderDoctorList(pageIndex = 0) {
        const doctorListElement = $("#doctor-list");
        let url = `/api/doctor/page/${pageIndex}`;
        const filterForm = $("#filter-form");
        if (filterForm) {
            url += "?" + SearchParamsUtils.toSearchParams(this.doctorDTO);
        }
        const data = await FetchingUtils.fetch(url);
        if (!data) {
            doctorListElement.innerHTML = "<p class='text-center w-100'>Không có bác sĩ nào</p>";
            return;
        }
        if ("doctors" in data) {
            const doctorData = data.doctors;
            doctorListElement.innerHTML = DoctorResponse.fromJsonArray(doctorData).map(doctor => doctor.setRenderStrategy(renderDoctorResponseForList).toHtml()).join("");
        }
        if ("currentPage" in data && "totalPages" in data) {
            const paginationElement = $("#doctor-pagination");
            const pagination = new Pagination(data.currentPage, data.totalPages);
            paginationElement.innerHTML = pagination.toHtml();
            pagination.setEvent(this.renderDoctorList.bind(this));
        }
    }

    setupFilterFormEvents() {
        const filterForm = $("#filter-form");
        if (!filterForm) return;
        filterForm.addEventListener("submit", async (event) => {
            const formData = new FormData(filterForm);
            this.doctorDTO = FormDataUtils.getObjectFromFormData(new DoctorDTO(), formData);
            event.preventDefault();
            await this.renderDoctorList();
        });
        filterForm.addEventListener("reset", async (event) => {
            this.doctorDTO = new DoctorDTO();
            await this.renderDoctorList();
        });
    }
}
