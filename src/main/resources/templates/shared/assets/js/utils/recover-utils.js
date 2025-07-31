import toast from "/templates/shared/assets/js/plugins/toast.js";
import {FetchingUtils} from "/templates/shared/assets/js/utils/fetching-utils.js";
import {FormDataUtils} from "/templates/shared/assets/js/utils/form-data.js";
import {Pagination} from "/templates/shared/assets/js/utils/pagination.js";
import {SearchParamsUtils} from "/templates/shared/assets/js/utils/search-params-utils.js";

export class RecoverUtils {
    /**
     * CrudUtils constructor
     * @param apiUrl
     * @param responseClass
     * @param renderStrategy
     * @param {string[]} filterSelectors
     * @param listSelector
     * @param paginationSelector
     * @param recoverButtonSelector
     * @param recoverFormSelector
     */
    constructor({
                    apiUrl,
                    responseClass = null,
                    renderStrategy = null,
                    filterSelectors = ["#filter"],
                    listSelector = "#list",
                    paginationSelector = "#pagination",
                    recoverButtonSelector = ".recover-button",
                    recoverFormSelector = "#recover-form"
                }) {
        this.apiUrl = apiUrl;
        this.responseClass = responseClass;
        this.renderStrategy = renderStrategy;
        this.filterSelectors = filterSelectors;
        this.listSelector = listSelector;
        this.paginationSelector = paginationSelector;
        this.recoverButtonSelector = recoverButtonSelector;
        this.recoverFormSelector = recoverFormSelector;
        this.dtoInstance = null;
    }

    async readData(pageIndex = 0) {
        const params = new SearchParamsUtils(this.dtoInstance);
        const data = await FetchingUtils.fetch(
                `${this.apiUrl}/page/${pageIndex}?${params}`
        );
        this.renderList(data);
        this.renderPagination(data);
        this.setupRecoverButtons();
    }

    async recoverData(id = null) {
        const data = await FetchingUtils.fetch(`${this.apiUrl}/${id}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(dtoInstance)
        });
        if (data !== null) {
            toast.success("Khôi phục thành công!", {
                position: "top-right",
                duration: 3000,
                progress: true
            });
            await this.readData();
        }
    }

    renderList(data) {
        const listElement = document.querySelector(this.listSelector);
        if (!listElement) return;
        listElement.innerHTML = ""; // Clear previous content
        if (
                !data ||
                !("items" in data) ||
                !Array.isArray(data.items) ||
                data.items.length === 0
        )
            return;
        listElement.innerHTML = this.responseClass
                .fromJsonArray(data.items)
                .map((item) => item.render())
                .join("");
    }

    renderPagination(data) {
        const paginationElement = document.querySelector(this.paginationSelector);
        if (!paginationElement) return;
        paginationElement.innerHTML = ""; // Clear previous content

        if (!data || !("currentPage" in data) || !("totalPages" in data)) return;
        const pagination = new Pagination(data.currentPage, data.totalPages);
        paginationElement.innerHTML = pagination.render();
        pagination.setEvent(this.readData.bind(this));
    }

    setupRecoverButtons() {
        const recoverButtons = document.querySelectorAll(
                this.recoverButtonSelector
        );
        if (!recoverButtons || recoverButtons.length === 0) return;
        recoverButtons.forEach((button) => {
            button.addEventListener("click", (event) => {
                const item = event.target.closest(".item");
                if (!item) return;
                const id = item.querySelector("[name=id]").value;
                if (!id) return;
                const recoverForm = document.querySelector(this.recoverFormSelector);
                if (!recoverForm) return;
                recoverForm.addEventListener("submit", async (event) => {
                    const submitButton = recoverForm.querySelector("[type='submit']");
                    if (submitButton) {
                        submitButton.disabled = true; // Disable the button to prevent multiple submissions
                    }
                    event.preventDefault();
                    await this.recoverData(id);
                    if (submitButton) {
                        submitButton.disabled = false; // Re-enable the button after submission
                    }
                });
            });
        });
    }

    setupFilterFormEvents() {
        this.filterSelectors.forEach((filterSelector) => {
            const filterForm = document.querySelector(filterSelector);
            if (!filterForm) return;
            filterForm.addEventListener("submit", async (event) => {
                const submitButton = filterForm.querySelector("[type='submit']");
                if (submitButton) {
                    submitButton.disabled = true; // Disable the button to prevent multiple submissions
                }
                event.preventDefault();
                const formData = new FormData(filterForm);
                this.dtoInstance = FormDataUtils.getObjectFromFormData(
                        this.dtoInstance,
                        formData
                );
                await this.readData(); // Reset to the first page on filter
                if (submitButton) {
                    submitButton.disabled = false; // Re-enable the button after submission
                }
            });
            filterForm.addEventListener("reset", async (event) => {
                const submitButton = filterForm.querySelector("[type='submit']");
                if (submitButton) {
                    submitButton.disabled = true; // Disable the button to prevent multiple submissions
                }
                event.preventDefault();
                this.dtoInstance = null; // Reset the DTO instance
                await this.readData(); // Reset to the first page on reset
                if (submitButton) {
                    submitButton.disabled = false; // Re-enable the button after submission
                }
            });
        });
    }

    setupFormEvents() {
        this.setupFilterFormEvents();
    }

    async init() {
        await this.readData();
        this.setupFormEvents();
    }
}
