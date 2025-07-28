import toast from "/templates/shared/assets/js/plugins/toast.js";
import {FetchingUtils} from "/templates/shared/assets/js/utils/fetching-utils.js";
import {FormDataUtils} from "/templates/shared/assets/js/utils/form-data.js";
import {Pagination} from "/templates/shared/assets/js/utils/pagination.js";
import {SearchParamsUtils} from "/templates/shared/assets/js/utils/search-params-utils.js";

export class CrudUtils {
    /**
     * CrudUtils constructor
     * @param apiUrl
     * @param responseClass
     * @param renderStrategy
     * @param {string[]} filterSelectors
     * @param listSelector
     * @param paginationSelector
     * @param deleteButtonSelector
     * @param editButtonSelector
     * @param addFormSelector
     * @param editFormSelector
     * @param deleteFormSelector
     */
    constructor({
                    apiUrl,
                    responseClass = null,
                    renderStrategy = null,
                    filterSelectors = ["#filter"],
                    listSelector = "#list",
                    paginationSelector = "#pagination",
                    deleteButtonSelector = ".delete-button",
                    editButtonSelector = ".edit-button",
                    addFormSelector = "#addForm",
                    editFormSelector = "#editForm",
                    deleteFormSelector = "#deleteForm"
                }) {
        this.apiUrl = apiUrl;
        this.responseClass = responseClass;
        this.renderStrategy = renderStrategy;
        this.filterSelectors = filterSelectors;
        this.listSelector = listSelector;
        this.paginationSelector = paginationSelector;
        this.deleteButtonSelector = deleteButtonSelector;
        this.editButtonSelector = editButtonSelector;
        this.addFormSelector = addFormSelector;
        this.editFormSelector = editFormSelector;
        this.deleteFormSelector = deleteFormSelector;
        this.dtoInstance = null;
    }

    async readData(pageIndex = 0) {
        const params = new SearchParamsUtils(this.dtoInstance);
        const data = await FetchingUtils.fetch(`${this.apiUrl}/page/${pageIndex}?${params}`);
        this.renderList(data);
        this.renderPagination(data);
        this.setupUpdateButtons();
        this.setupDeleteButtons();
    }

    async createData(dtoInstance = null) {
        const data = await FetchingUtils.fetch(`${this.apiUrl}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(dtoInstance)
        });
        if (data !== null) {
            toast.success("Thêm thành công!", {
                position: "top-right",
                duration: 3000,
                progress: true
            });
            await this.readData();
        }
    }

    async updateData(dtoInstance = null) {
        const data = await FetchingUtils.fetch(`${this.apiUrl}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(dtoInstance)
        });
        if (data !== null) {
            toast.success("Cập nhật thành công!", {
                position: "top-right",
                duration: 3000,
                progress: true
            });
            await this.readData();
        }
    }

    async deleteData(id = null) {
        const data = await FetchingUtils.fetch(`${this.apiUrl}/${id}`, {
            method: "DELETE"
        });
        if (data !== null) {
            toast.success("Xóa thành công!", {
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
        if (!data || !("items" in data) || !Array.isArray(data.items) || data.items.length === 0) return;
        listElement.innerHTML = this.responseClass.fromJsonArray(data.items)
                .map(item => item.render())
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

    setupUpdateButtons() {
        const editButtons = document.querySelectorAll(this.editButtonSelector);
        editButtons.forEach(button => {
            button.addEventListener("click", (event) => {
                const itemElement = event.target.closest(".item");
                if (!itemElement) return;
                const dataElements = itemElement.querySelectorAll("[data-name]");
                if (!dataElements) return;
                const updateForm = document.querySelector(this.editFormSelector);
                if (!updateForm) return;
                dataElements.forEach(element => {
                    const name = element.getAttribute("data-name");
                    const value = element.textContent.trim();
                    const input = updateForm.querySelector(`[name="${name}"]`);
                    if (input) {
                        input.value = value;
                    }
                });
            });
        });
    }

    setupDeleteButtons() {
        const deleteButtons = document.querySelectorAll(this.deleteButtonSelector);
        deleteButtons.forEach(async (button) => {
            const itemElement = button.closest(".item");
            if (!itemElement) return;
            const id = itemElement.querySelector("[name='id']").value;
            if (!id) return;
            const deleteForm = document.querySelector(this.deleteFormSelector);
            if (!deleteForm) {
                if (confirm("Bạn có chắc chắn muốn xóa không?")) {
                    await this.deleteData(id);
                }
                return; // If delete form is not found, delete directly
            }
            deleteForm.addEventListener("submit", async (event) => {
                const submitButton = deleteForm.querySelector("[type='submit']");
                if (submitButton) {
                    submitButton.disabled = true; // Disable the button to prevent multiple submissions
                }
                event.preventDefault();
                await this.deleteData(id);
                if (submitButton) {
                    submitButton.disabled = false; // Re-enable the button after submission
                }
            });
        });
    }

    setupFilterFormEvents() {
        this.filterSelectors.forEach(filterSelector => {
            const filterForm = document.querySelector(filterSelector);
            if (!filterForm) return;
            filterForm.addEventListener("submit", async (event) => {
                const submitButton = filterForm.querySelector("[type='submit']");
                if (submitButton) {
                    submitButton.disabled = true; // Disable the button to prevent multiple submissions
                }
                event.preventDefault();
                const formData = new FormData(filterForm);
                this.dtoInstance = FormDataUtils.getObjectFromFormData(this.dtoInstance, formData);
                await this.readData(0); // Reset to the first page on filter
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
                await this.readData(0); // Reset to the first page on reset
                if (submitButton) {
                    submitButton.disabled = false; // Re-enable the button after submission
                }
            });
        });
    }

    setupAddFormEvents() {
        const addForm = document.querySelector(this.addFormSelector);
        if (!addForm) return;
        addForm.addEventListener("submit", async (event) => {
            const submitButton = addForm.querySelector("[type='submit']");
            if (submitButton) {
                submitButton.disabled = true; // Disable the button to prevent multiple submissions
            }
            event.preventDefault();
            const formData = new FormData(addForm);
            this.dtoInstance = FormDataUtils.getObjectFromFormData(null, formData);
            await this.createData();
            if (submitButton) {
                submitButton.disabled = false; // Re-enable the button after submission
            }
        });
    }

    setupEditFormEvents() {
        const editForm = document.querySelector(this.editFormSelector);
        if (!editForm) return;
        editForm.addEventListener("submit", async (event) => {
            const submitButton = editForm.querySelector("[type='submit']");
            if (submitButton) {
                submitButton.disabled = true; // Disable the button to prevent multiple submissions
            }
            event.preventDefault();
            const formData = new FormData(editForm);
            this.dtoInstance = FormDataUtils.getObjectFromFormData(null, formData);
            await this.updateData();
            if (submitButton) {
                submitButton.disabled = false; // Re-enable the button after submission
            }
        });
    }

    setupFormEvents() {
        this.setupAddFormEvents();
        this.setupEditFormEvents();
        this.setupFilterFormEvents();
    }

    async init() {
        await this.readData();
        this.setupFormEvents();
    }
}