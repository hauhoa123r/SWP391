import {HospitalDTO} from "/templates/shared/assets/js/model/dto/HospitalDTO.js";
import {
    HospitalResponse, renderHospitalResponseForList
} from "/templates/shared/assets/js/model/response/HospitalResponse.js";
import {FetchingUtils} from "/templates/shared/assets/js/utils/fetching-utils.js";
import {FormDataUtils} from "/templates/shared/assets/js/utils/form-data.js";
import {Pagination} from "/templates/shared/assets/js/utils/pagination.js";
import {SearchParamsUtils} from "/templates/shared/assets/js/utils/search-params-utils.js";

const $ = document.querySelector.bind(document);
const $$ = document.querySelectorAll.bind(document);

document.addEventListener("DOMContentLoaded", async () => {
    const hospitalRenderer = new RenderHospital();
    await hospitalRenderer.fetchHospitals();
    hospitalRenderer.setupFormEvents();
});

class RenderHospital {
    constructor() {
        this.hospitalDTO = new HospitalDTO();
    }

    async fetchHospitals(pageIndex = 0) {
        const params = SearchParamsUtils.toSearchParams(this.hospitalDTO);
        const data = await FetchingUtils.fetch(`/api/hospital/page/${pageIndex}?${params}`);
        this.renderHospitals(data);
        this.renderPagination(data);
    }

    renderHospitals(data) {
        const hospitalListElement = $("#hospital-list");
        hospitalListElement.innerHTML = ""; // Clear previous content
        if (!data || !("hospitals" in data) || data.hospitals.length === 0) return;

        hospitalListElement.innerHTML = HospitalResponse.fromJsonArray(data.hospitals).map(hospital => hospital.setRenderStrategy(renderHospitalResponseForList).toHtml()).join("");
    }

    renderPagination(data) {
        const paginationElement = $("#pagination");
        paginationElement.innerHTML = ""; // Clear previous content
        if (!data || !("currentPage" in data) || !("totalPages" in data)) return;
        const pagination = new Pagination(data.currentPage, data.totalPages);
        paginationElement.innerHTML = pagination.toHtml();
        pagination.setEvent(this.fetchHospitals.bind(this));
    }

    setupFormEvents() {
        const hospitalFilterForm = $("#hospital-filter-form");
        hospitalFilterForm.addEventListener("submit", async (event) => {
            event.preventDefault();
            const formData = new FormData(hospitalFilterForm);
            this.hospitalDTO = FormDataUtils.getObjectFromFormData(new HospitalDTO(), formData);
            await this.fetchHospitals(0); // Reset to first page on new search
        });
        hospitalFilterForm.addEventListener("reset", async () => {
            this.hospitalDTO = new HospitalDTO(); // Reset DTO
            await this.fetchHospitals(0); // Reset to first page on reset
        });
    }
}