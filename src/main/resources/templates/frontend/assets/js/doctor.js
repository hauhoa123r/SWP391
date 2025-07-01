import toast from "/templates/frontend/assets/js/plugins/toast.js";
import {
    DepartmentResponse, renderDepartmentResponseForList
} from "/templates/frontend/assets/js/model/response/DepartmentResponse.js";
import {Pagination} from "/templates/frontend/assets/js/utils/Pagination.js";
import {
    DoctorResponse, renderDoctorResponseForList
} from "/templates/frontend/assets/js/model/response/DoctorResponse.js";
import {fetchData} from "/templates/frontend/assets/js/utils/Fetching.js";

const $ = document.querySelector.bind(document);
const $$ = document.querySelectorAll.bind(document);

document.addEventListener("DOMContentLoaded", async () => {
    await renderDepartmentList();
    await renderDoctorList();
});

async function renderDepartmentList(departmentId) {
    const departmentListElement = $("#department-list");
    const departmentData = await fetchData("/api/department");
    const allDepartmentElement = `
    <button aria-controls="doc-all" aria-selected="true"
        class="nav-link ${departmentId ? "" : "active"}" type="button">All
    </button>
    `;
    if (departmentData) {
        departmentListElement.innerHTML = allDepartmentElement + DepartmentResponse.fromJsonArray(departmentData).map(department => department.setRenderStrategy(renderDepartmentResponseForList).toHtml(departmentId)).join("");
    }
    departmentListElement.querySelectorAll("button").forEach(button => {
        button.addEventListener("click", (event) => {
            departmentListElement.querySelectorAll("button").forEach(btn => btn.classList.remove("active"));
            event.currentTarget.classList.add("active");
            renderDoctorList();
        });
    });
}

async function renderDoctorList(pageIndex = 0) {
    const doctorListElement = $("#doctor-list");
    let url = `/api/doctor/page/${pageIndex}`;
    const selectedDepartmentElement = $("#department-list .nav-link.active");
    if (selectedDepartmentElement) {
        const departmentId = selectedDepartmentElement.dataset.department;
        if (departmentId) {
            url += `/department/${departmentId}`;
        }
    }
    const data = await fetchData(url);
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