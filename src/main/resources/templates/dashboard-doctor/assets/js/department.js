import {DepartmentDTO} from "/templates/shared/assets/js/model/dto/DepartmentDTO.js";
import {
    DepartmentResponse, renderDepartmentResponseForList
} from "/templates/shared/assets/js/model/response/DepartmentResponse.js";
import {FetchingUtils} from "/templates/shared/assets/js/utils/fetching-utils.js";
import {FormDataUtils} from "/templates/shared/assets/js/utils/form-data.js";
import {Pagination} from "/templates/shared/assets/js/utils/pagination.js";
import {SearchParamsUtils} from "/templates/shared/assets/js/utils/search-params-utils.js";

const $ = document.querySelector.bind(document);
const $$ = document.querySelectorAll.bind(document);

document.addEventListener("DOMContentLoaded", async () => {
    const renderDepartment = new RenderDepartment();
    await renderDepartment.fetchDepartments();
    renderDepartment.setupFormEvents();
});

class RenderDepartment {
    constructor() {
        this.departmentDTO = new DepartmentDTO();
    }

    async fetchDepartments(pageIndex = 0) {
        const params = SearchParamsUtils.toSearchParams(this.departmentDTO);
        const data = await FetchingUtils.fetch(`/api/department/page/${pageIndex}?${params}`);
        this.renderDepartments(data);
        this.renderPagination(data);
    }

    renderDepartments(data) {
        const departmentListElement = $("#department-list");
        departmentListElement.innerHTML = ""; // Clear previous content

        if (!data || !("departments" in data) || data.departments.length === 0) return;

        departmentListElement.innerHTML = DepartmentResponse.fromJsonArray(data.departments).map(department => department.setRenderStrategy(renderDepartmentResponseForList).toHtml()).join("");
    }

    renderPagination(data) {
        const paginationElement = $("#pagination");
        paginationElement.innerHTML = ""; // Clear previous content

        if (!data || !("currentPage" in data) || !("totalPages" in data)) return;

        const pagination = new Pagination(data.currentPage, data.totalPages);
        paginationElement.innerHTML = pagination.render();
        pagination.setEvent(this.fetchDepartments.bind(this));
    }

    setupFormEvents() {
        const departmentFilterForm = $("#department-filter-form");
        if (!departmentFilterForm) return;
        departmentFilterForm.addEventListener("submit", async (event) => {
            event.preventDefault();
            const formData = new FormData(departmentFilterForm);
            this.departmentDTO = FormDataUtils.getObjectFromFormData(new DepartmentDTO(), formData);
            await this.fetchDepartments(0); // Reset to first page on filter change
        });
        departmentFilterForm.addEventListener("reset", async (event) => {
            event.preventDefault();
            this.departmentDTO = new DepartmentDTO(); // Reset to default DTO
            await this.fetchDepartments(0); // Reset to first page on reset
        });
    }
}