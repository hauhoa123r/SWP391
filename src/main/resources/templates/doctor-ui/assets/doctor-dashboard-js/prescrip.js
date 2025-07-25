document.addEventListener("DOMContentLoaded", function () {
    const medicineTableBody = document.querySelector("#medicineTable tbody");
    const searchMedicineInput = document.getElementById("searchMedicine");
    const btnSearch = document.getElementById("btnSearch");
    const pagination = document.getElementById("paginationPre");

    let currentPage = 0;
    const pageSize = 10;
    let currentKeyword = "";

    let medicinesCache = [];
    let expandedRows = new Set();

    async function fetchMedicines(keyword, page, size) {
        try {
            let url = `/api/medicines?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`;
            const res = await fetch(url);
            if (!res.ok) throw new Error("Lỗi tải danh sách thuốc");
            const data = await res.json();
            renderMedicineTable(data.content);
            renderPagination(data.totalPages, data.number);
            medicinesCache = data.content;

        } catch (error) {
            alert(error.message);
        }
    }

    function renderMedicineTable(medicines) {
        medicineTableBody.innerHTML = "";

        medicines.forEach((med) => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
        <td>${med.name}</td>
        <td>
          <button class="btn btn-info btn-sm view-detail-btn" data-id="${med.id}" aria-label="Xem chi tiết thuốc">
            <i class="fas fa-eye"></i>
          </button>
        </td>
        <td><input type="number" min="1" value="1" class="form-control quantity-input" style="width: 80px;"></td>
        <td>
          <button class="btn btn-primary btn-sm select-medicine-btn" data-id="${med.id}">Chọn</button>
        </td>
      `;
            medicineTableBody.appendChild(tr);

            if (expandedRows.has(med.id)) {
                const trDetail = document.createElement("tr");
                trDetail.classList.add("medicine-detail-row");
                trDetail.innerHTML = `
  <td colspan="4">
    <div><strong>Mô tả:</strong> ${med.description || "Không có thông tin"}</div>
    <div><strong>Giá:</strong> ${med.price || "Chưa có"}</div>
    ${
                    med.imageUrl
                        ? `<div><img src="${med.imageUrl}" alt="${med.name}"></div>`
                        : ""
                }
  </td>
`;

                medicineTableBody.appendChild(trDetail);
            }
        });

        // Gắn sự kiện hiển thị chi tiết
        medicineTableBody.querySelectorAll(".view-detail-btn").forEach((btn) => {
            btn.onclick = () => {
                const id = parseInt(btn.dataset.id);
                if (expandedRows.has(id)) {
                    expandedRows.delete(id);
                } else {
                    expandedRows.add(id);
                }
                // Rerender bảng dùng medicinesCache
                renderMedicineTable(medicinesCache);
            };
        });

        // Gắn sự kiện nút Chọn thuốc (tùy bạn xử lý)
        medicineTableBody.querySelectorAll(".select-medicine-btn").forEach((btn) => {
            btn.onclick = () => {
                const id = parseInt(btn.dataset.id);
                const med = medicinesCache.find((m) => m.id === id);
                const quantityInput = btn.closest("tr").querySelector(".quantity-input");
                const quantity = parseInt(quantityInput.value) || 1;
                console.log("Chọn thuốc:", med, "Số lượng:", quantity);
                // TODO: Xử lý lưu thuốc vào đơn thuốc ở đây
            };
        });
    }

    function renderPagination(totalPages, currentPageIndex) {
        pagination.innerHTML = "";

        // Nút prev
        const prevLi = document.createElement("li");
        prevLi.className = `page-item ${currentPageIndex === 0 ? "disabled" : ""}`;
        prevLi.innerHTML = `<a class="page-link" href="#" aria-label="Trang trước">&laquo;</a>`;
        prevLi.onclick = (e) => {
            e.preventDefault();
            if (currentPageIndex > 0) {
                goToPage(currentPageIndex - 1);
            }
        };
        pagination.appendChild(prevLi);

        // Các nút trang (hiển thị 1 số trang cơ bản)
        const maxPagesToShow = 7;
        let startPage = Math.max(0, currentPageIndex - Math.floor(maxPagesToShow / 2));
        let endPage = Math.min(totalPages - 1, startPage + maxPagesToShow - 1);
        startPage = Math.max(0, endPage - maxPagesToShow + 1);

        for (let i = startPage; i <= endPage; i++) {
            const li = document.createElement("li");
            li.className = `page-item ${i === currentPageIndex ? "active" : ""}`;
            li.innerHTML = `<a class="page-link" href="#">${i + 1}</a>`;
            li.onclick = (e) => {
                e.preventDefault();
                goToPage(i);
            };
            pagination.appendChild(li);
        }

        // Nút next
        const nextLi = document.createElement("li");
        nextLi.className = `page-item ${currentPageIndex === totalPages - 1 ? "disabled" : ""}`;
        nextLi.innerHTML = `<a class="page-link" href="#" aria-label="Trang sau">&raquo;</a>`;
        nextLi.onclick = (e) => {
            e.preventDefault();
            if (currentPageIndex < totalPages - 1) {
                goToPage(currentPageIndex + 1);
            }
        };
        pagination.appendChild(nextLi);
    }

    function goToPage(page) {
        currentPage = page;
        fetchMedicines(currentKeyword, currentPage, pageSize);
    }

    // Bắt sự kiện tìm kiếm
    btnSearch.onclick = () => {
        currentKeyword = searchMedicineInput.value.trim();
        currentPage = 0;
        fetchMedicines(currentKeyword, currentPage, pageSize);
    };

    // Tự động load trang đầu tiên khi mở modal hoặc page load
    fetchMedicines(currentKeyword, currentPage, pageSize);
});
