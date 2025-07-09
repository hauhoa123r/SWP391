import {
    renderServiceResponseForList, ServiceResponse
} from "/templates/frontend/assets/js/model/response/ServiceResponse.js";
import {Pagination} from "/templates/frontend/assets/js/utils/Pagination.js";
import {fetchData} from "/templates/frontend/assets/js/utils/Fetching.js";

const $ = document.querySelector.bind(document);
const $$ = document.querySelectorAll.bind(document);

document.addEventListener("DOMContentLoaded", async () => {
    await renderServiceList();
});

async function renderServiceList(pageIndex = 0) {
    const serviceListElement = $("#service-list");
    let url = `/api/service/page/${pageIndex}`;
    const data = await fetchData(url);
    if (!data) {
        serviceListElement.innerHTML = "<p class='text-center'>No services available</p>";
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
        pagination.setEvent(renderServiceList);
    }
}