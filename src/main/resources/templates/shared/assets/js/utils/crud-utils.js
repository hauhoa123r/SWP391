import toast from "/templates/shared/assets/js/plugins/toast.js";
import {Base64Utils} from "/templates/shared/assets/js/utils/base64-utils.js";
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
     * @param customButtonSelector
     * @param customButtonEvent
     * @param customFormSelector
     * @param customFormEvent
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
                    deleteFormSelector = "#deleteForm",
                    customButtonSelector = null,
                    customButtonEvent = null,
                    customFormSelector = null,
                    customFormEvent = null
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
        this.customButtonSelector = customButtonSelector;
        this.customButtonEvent = customButtonEvent;
        this.customFormSelector = customFormSelector;
        this.customFormEvent = customFormEvent;
    }

    async readData(pageIndex = 0) {
        const params = SearchParamsUtils.toSearchParams(this.dtoInstance);
        const data = await FetchingUtils.fetch(`${this.apiUrl}/page/${pageIndex}?${params}`);
        this.renderList(data);
        this.renderPagination(data);
        this.setupUpdateButtons();
        this.setupDeleteButtons();
        this.setupCustomButtons();
    }

    async createData(dtoInstance = null) {
        for (const key of Object.keys(dtoInstance)) {
            const value = dtoInstance[key];
            if (!value) continue;
            if (value instanceof File) {
                dtoInstance[key] = await Base64Utils.getBase64(value);
            }
        }

        const data = await FetchingUtils.fetch(`${this.apiUrl}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(dtoInstance)
        }, "text");
        if (data !== null) {
            toast.success("Thêm thành công!", {
                icon: true,
                duration: 3000,
                progress: true
            });
            await this.readData();
            const addForm = document.querySelector(this.addFormSelector);
            addForm.reset();
            if (!addForm) return;
            const closeButton = addForm.querySelector("[data-bs-dismiss]");
            if (closeButton) {
                closeButton.click(); // Close the modal if it exists
            }
        }
    }

    async updateData(dtoInstance = null) {
        for (const key of Object.keys(dtoInstance)) {
            const value = dtoInstance[key];
            if (!value) continue;
            if (value instanceof File) {
                dtoInstance[key] = await Base64Utils.getBase64(value);
            }
        }

        const data = await FetchingUtils.fetch(`${this.apiUrl}/${dtoInstance.id}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(dtoInstance)
        }, "text");
        if (data !== null) {
            toast.success("Cập nhật thành công!", {
                icon: true,
                duration: 3000,
                progress: true
            });
            await this.readData();
            const editForm = document.querySelector(this.editFormSelector);
            if (!editForm) return;
            editForm.reset(); // Reset the form after submission
            const closeButton = editForm.querySelector("[data-bs-dismiss]");
            if (closeButton) {
                closeButton.click(); // Close the modal if it exists
            }
        }
    }

    async deleteData(id = null) {
        const data = await FetchingUtils.fetch(`${this.apiUrl}/${id}`, {
            method: "DELETE"
        }, "text");
        if (data !== null) {
            toast.success("Xóa thành công!", {
                icon: true,
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
                .map(item => {
                    item.setRenderStrategy(this.renderStrategy);
                    if (typeof item.toHtml === "function") {
                        return item.toHtml();
                    } else if (typeof item.render === "function") {
                        return item.render();
                    }
                })
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
                    let value = element.getAttribute("data-value") || element.textContent.trim();
                    if (value === "null" || value === "undefined") {
                        value = ""; // Normalize null/undefined values to empty string
                    }

                    const image = updateForm.querySelector(`img[data-name="${name}"]`);
                    if (image) {
                        image.src = value; // Assuming value is a URL for the image
                        if (value) {
                            image.classList.remove("d-none"); // Show the image if it was hidden
                        } else {
                            image.classList.add("d-none"); // Hide the image if value is empty
                        }
                        const input = updateForm.querySelector(`input[name="${name}"]`);
                        if (input) {
                            input.value = "";
                        }
                        return;
                    }

                    const input = updateForm.querySelector(`input[name="${name}"]`);
                    if (input) {
                        input.value = value;
                        return;
                    }

                    const textarea = updateForm.querySelector(`textarea[name="${name}"]`);
                    if (textarea) {
                        textarea.value = value;
                        return;
                    }

                    const select = updateForm.querySelector(`select[name="${name}"]`);
                    if (select) {
                        let option = Array.from(select.options).find(opt => opt.value === value);
                        if (option) {
                            select.value = option.value;
                        } else {
                            option = Array.from(select.options).find(opt => opt.textContent.trim() === value);
                            if (option) {
                                select.value = option.value;
                            }
                        }
                    }
                });
            });
        });
    }

    setupDeleteButtons() {
        const deleteButtons = document.querySelectorAll(this.deleteButtonSelector);
        deleteButtons.forEach(async (button) => {
            button.addEventListener("click", async (event) => {
                event.stopImmediatePropagation();
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
        });
    }

    setupCustomButtons() {
        if (!this.customButtonSelector || !this.customButtonEvent) return;
        const customButtons = document.querySelectorAll(this.customButtonSelector);
        customButtons.forEach(button => {
            button.addEventListener("click", async (event) => {
                event.preventDefault();
                const itemElement = button.closest(".item");
                if (!itemElement) return;
                await this.customButtonEvent(itemElement);
                await this.readData(); // Refresh the data after custom action
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
            const dtoInstance = FormDataUtils.getObjectFromFormData(null, formData);
            await this.createData(dtoInstance);
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
            const dtoInstance = FormDataUtils.getObjectFromFormData(null, formData);
            await this.updateData(dtoInstance);
            if (submitButton) {
                submitButton.disabled = false; // Re-enable the button after submission
            }

        });
    }

    setupCustomFormEvents() {
        if (!this.customFormSelector || !this.customFormEvent) return;
        const customForm = document.querySelector(this.customFormSelector);
        if (!customForm) return;
        customForm.addEventListener("submit", async (event) => {
            const submitButton = customForm.querySelector("[type='submit']");
            if (submitButton) {
                submitButton.disabled = true; // Disable the button to prevent multiple submissions
            }
            event.preventDefault();
            const formData = new FormData(customForm);
            const dtoInstance = FormDataUtils.getObjectFromFormData(null, formData);
            await this.customFormEvent(dtoInstance);
            if (submitButton) {
                submitButton.disabled = false; // Re-enable the button after submission
            }
        });
    }

    setupFormEvents() {
        this.setupAddFormEvents();
        this.setupEditFormEvents();
        this.setupFilterFormEvents();
        this.setupCustomFormEvents();
    }

    async init() {
        await this.readData();
        this.setupFormEvents();
    }
}