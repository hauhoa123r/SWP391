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
        <nav aria-label="Doctor pagination" class="mt-4">
            <ul class="pagination justify-content-center">
                <!-- Previous button -->
                <li class="page-item ${this.currentPage === 0 ? 'disabled' : ''}" data-page="${this.currentPage - 1}">
                    <a aria-disabled="${this.currentPage === 0 ? 'true' : 'false'}"
                       class="page-link"
                       tabindex="-1">Previous</a>
                </li>`;

        if (this.currentPage > 1) {
            html += `
                <li class="page-item" data-page="0">
                    <a class="page-link">1</a>
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
                <li class="page-item ${i === this.currentPage ? 'active' : ''}" data-page="${i}">
                    <a class="page-link">${i + 1}</a>
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
                    <a class="page-link">${this.totalPages}</a>
                </li>
            `;
        }

        html += `
               <li class="page-item ${this.currentPage === this.totalPages - 1 ? 'disabled' : ''}" data-page="${this.currentPage + 1}">
                    <a class="page-link"
                          aria-disabled="${this.currentPage === this.totalPages - 1 ? 'true' : 'false'}">Next</a>
                </li>
            </ul>
        </nav>   
        `;

        return html;
    }

    setEvent(callback) {
        document.querySelectorAll(".page-item[data-page]").forEach(element => {
            element.addEventListener("click", function () {
                const pageIndex = parseInt(this.getAttribute("data-page"));
                if (callback) {
                    callback(pageIndex);
                }
            });
        });
    }
}