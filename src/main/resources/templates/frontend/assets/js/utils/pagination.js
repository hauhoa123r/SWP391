export class Pagination {
    constructor(currentPage, totalPages) {
        this.currentPage = currentPage;
        this.totalPages = totalPages;
    }

    static fromJson(json) {
        return new Pagination(json.currentPage, json.totalPages);
    }

    toHtml() {

        let html = `
        <nav aria-label="Pagination" class="mt-4">
            <ul class="pagination justify-content-center">
                <!-- Previous button -->
                <li class="page-item ${this.currentPage === 0 ? "disabled" : ""}" data-page="${this.currentPage - 1}">
                    <a href aria-disabled="${this.currentPage === 0 ? "true" : "false"}"
                       class="page-link"
                       tabindex="-1">Trang trước</a>
                </li>`;

        if (this.currentPage > 1) {
            html += `
                <li class="page-item" data-page="0">
                    <a href class="page-link">1</a>
                </li>
            `;
        }

        if (this.currentPage > 2) {
            html += `
                <li class="page-item disabled">
                    <span class="page-link">...</span>
                </li> 
            `;
        }

        for (let i = Math.max(0, this.currentPage - 1); i <= Math.min(this.totalPages - 1, this.currentPage + 1); i++) {
            html += `
                <li class="page-item ${i === this.currentPage ? "active" : ""}" data-page="${i}">
                    <a href class="page-link">${i + 1}</a>
                </li>
            `;
        }

        if (this.currentPage < this.totalPages - 3) {
            html += `
                <li class="page-item disabled">
                    <span class="page-link">...</span>
                </li>
            `;
        }

        if (this.currentPage < this.totalPages - 2) {
            html += `
                <li class="page-item" data-page="${this.totalPages - 1}">
                    <a href class="page-link">${this.totalPages}</a>
                </li>
            `;
        }

        html += `
               <li class="page-item ${this.currentPage === this.totalPages - 1 ? "disabled" : ""}" data-page="${this.currentPage + 1}">
                    <a href class="page-link"
                          aria-disabled="${this.currentPage === this.totalPages - 1 ? "true" : "false"}">
                          Trang sau
                          </a>
                </li>
            </ul>
        </nav>   
        `;

        return html;
    }

    setEvent(callback) {
        document.querySelectorAll(".page-item[data-page]").forEach(element => {
            element.addEventListener("click", (event) => {
                if (element.classList.contains("disabled")) return; // Ignore clicks on disabled items
                event.preventDefault();
                const pageIndex = parseInt(event.currentTarget.getAttribute("data-page"));
                if (callback) {
                    callback(pageIndex);
                }
            });
        });
    }
}