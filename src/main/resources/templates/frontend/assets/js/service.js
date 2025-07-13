import {ServiceDTO} from "/templates/frontend/assets/js/model/dto/ServiceDTO.js";
import {
    renderServiceResponseForList, ServiceResponse
} from "/templates/frontend/assets/js/model/response/ServiceResponse.js";
import {FetchingUtils} from "/templates/frontend/assets/js/utils/fetching-utils.js";
import {FormDataUtils} from "/templates/frontend/assets/js/utils/form-data.js";
import {Pagination} from "/templates/frontend/assets/js/utils/pagination.js";
import {SearchParamsUtils} from "/templates/frontend/assets/js/utils/search-params-utils.js";

const $ = document.querySelector.bind(document);
const $$ = document.querySelectorAll.bind(document);

document.addEventListener("DOMContentLoaded", async () => {
    const renderService = new RenderService();
    await renderService.renderServiceList();
    renderService.setupFilterFormEvents();
});


class RenderService {
    constructor() {
        this.serviceDTO = new ServiceDTO();
    }

    async renderServiceList(pageIndex = 0) {
        let params = SearchParamsUtils.toSearchParams(this.serviceDTO);
        const serviceListElement = $("#service-list");
        let url = `/api/service/page/${pageIndex}?${params}`;
        const data = await FetchingUtils.fetch(url);

        if (!data) {
            serviceListElement.innerHTML = "<p class='text-center w-100'>Không có dịch vụ nào</p>";
            return;
        }

        if ("services" in data) {
            const serviceData = data.services;
            serviceListElement.innerHTML = ServiceResponse.fromJsonArray(serviceData).map(service => service.setRenderStrategy(renderServiceResponseForList).toHtml()).join("");
        }

        if ("currentPage" in data && "totalPages" in data) {
            const paginationElement = $("#service-pagination");
            const pagination = new Pagination(data.currentPage, data.totalPages);
            paginationElement.innerHTML = pagination.toHtml();
            pagination.setEvent(this.renderServiceList.bind(this));
        }
    }

    setupFilterFormEvents() {
        const filterFormElement = $("#filter-form");
        if (!filterFormElement) return;
        filterFormElement.addEventListener("submit", async (event) => {
            event.preventDefault();
            this.serviceDTO = FormDataUtils.getObjectFromFormData(new ServiceDTO(), new FormData(filterFormElement));
            await this.renderServiceList();
        });
        filterFormElement.addEventListener("reset", async () => {
            this.serviceDTO = new ServiceDTO();
            await this.renderServiceList();
        });
    }
}