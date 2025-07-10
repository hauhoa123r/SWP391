import {DoctorDTO} from "/templates/frontend/assets/js/model/dto/DoctorDTO.js";
import {
    DepartmentResponse, renderDepartmentResponseForSelect
} from "/templates/frontend/assets/js/model/response/DepartmentResponse.js";
import {
    DoctorResponse, renderDoctorResponseForList
} from "/templates/frontend/assets/js/model/response/DoctorResponse.js";
import toast from "/templates/frontend/assets/js/plugins/toast.js";
import {FetchingUtils} from "/templates/frontend/assets/js/utils/fetching-utils.js";
import {FormDataUtils} from "/templates/frontend/assets/js/utils/form-data.js";
import {Pagination} from "/templates/frontend/assets/js/utils/pagination.js";
import {SearchParamsUtils} from "/templates/frontend/assets/js/utils/search-params-utils.js";

const $ = document.querySelector.bind(document);
const $$ = document.querySelectorAll.bind(document);

document.addEventListener("DOMContentLoaded", async () => {
    await renderDoctorList();
    await renderDepartmentOption();
    setupFormSubmitEvent();
});

async function renderDepartmentOption(departmentId) {
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

async function renderDoctorList(pageIndex = 0) {
    const doctorListElement = $("#doctor-list");
    let url = `/api/doctor/page/${pageIndex}`;
    const filterForm = $("#filter-form");
    if (filterForm) {
        const formData = new FormData(filterForm);
        const doctorDTO = FormDataUtils.getObjectFromFormData(new DoctorDTO(), formData);
        console.log(doctorDTO);
        url += "?" + SearchParamsUtils.toSearchParams(doctorDTO);
    }
    const data = await FetchingUtils.fetch(url);
    if (!data) {
        toast.danger("Failed to load doctor data", {
            duration: 3000,
            position: "top-right",
            icon: true
        });
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
        pagination.setEvent(renderDoctorList);
    }
}

function setupFormSubmitEvent() {
    const filterForm = $("#filter-form");
    if (!filterForm) return;
    filterForm.addEventListener("submit", async (event) => {
        event.preventDefault();
        await renderDoctorList();
    });
    filterForm.addEventListener("reset", async (event) => {
        await event.target.reset();
        await renderDoctorList();
    });
}