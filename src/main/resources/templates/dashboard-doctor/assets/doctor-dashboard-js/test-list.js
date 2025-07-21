let currentPage = 0;
const pageSize = 8;
let currentSearchTerm = '';
let selectedTests = new Map();

function openTestModal() {
    selectedTests.clear();
    updateSelectedCount();
    updateSelectedTestsDisplay();
    const modal = new bootstrap.Modal(document.getElementById('testModal'));
    modal.show();
    loadTestTypes();
}

function loadTestTypes(page = 0) {
    currentPage = page;
    const testListBody = document.getElementById('testListBody');
    testListBody.innerHTML = '<tr><td colspan="3" class="text-center text-muted">Đang tải danh sách xét nghiệm...</td></tr>';

    let url = `/api/test-types?page=${page}&size=${pageSize}`;
    if (currentSearchTerm) {
        url += `&search=${encodeURIComponent(currentSearchTerm)}`;
    }
    fetch(url)
        .then(response => response.json())
        .then(data => {
            renderTestList(data.content);
            renderPagination(data.totalPages, page);
        })
        .catch(error => {
            console.error('Error:', error);
            testListBody.innerHTML = '<tr><td colspan="3" class="text-center text-danger">Đã xảy ra lỗi khi tải danh sách</td></tr>';
        });
}

function renderTestList(tests) {
    const testListBody = document.getElementById('testListBody');
    testListBody.innerHTML = '';

    if (tests.length === 0) {
        testListBody.innerHTML = '<tr><td colspan="3" class="text-center text-muted">Không tìm thấy xét nghiệm phù hợp</td></tr>';
        return;
    }

    tests.forEach(test => {
        const row = document.createElement('tr');
        const isChecked = selectedTests.has(test.id);
        row.innerHTML = `
            <td>${test.id}</td>
            <td>${test.testTypeName}</td>
            <td>
                <input type="checkbox" class="test-checkbox"
                       data-test-id="${test.id}"
                       data-test-name="${test.testTypeName}"
                       ${isChecked ? 'checked' : ''}>
            </td>
        `;
        testListBody.appendChild(row);
    });

    document.querySelectorAll('.test-checkbox').forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            const testId = this.dataset.testId;
            const testName = this.dataset.testName;

            if (this.checked) {
                selectedTests.set(testId, testName);
            } else {
                selectedTests.delete(testId);
            }
            updateSelectedCount();
            updateSelectAllCheckbox();
            updateSelectedTestsDisplay();
        });
    });

    updateSelectAllCheckbox();
}

function renderPagination(totalPages, currentPage) {
    const pagination = document.getElementById('pagination');
    pagination.innerHTML = '';

    if (totalPages <= 1) return;

    const prevLi = document.createElement('li');
    prevLi.className = `page-item ${currentPage <= 0 ? 'disabled' : ''}`;
    prevLi.innerHTML = `<a class="page-link" href="#" ${currentPage > 0 ? `onclick="loadTestTypes(${currentPage - 1})"` : ''}>«</a>`;
    pagination.appendChild(prevLi);

    addPageItem(pagination, 0, currentPage);
    if (totalPages > 1) {
        const startPage = Math.max(1, currentPage - 1);
        const endPage = Math.min(totalPages - 2, currentPage + 1);

        if (startPage > 1) addEllipsis(pagination);
        for (let i = startPage; i <= endPage; i++) {
            addPageItem(pagination, i, currentPage);
        }
        if (endPage < totalPages - 2) addEllipsis(pagination);
        addPageItem(pagination, totalPages - 1, currentPage);
    }

    const nextLi = document.createElement('li');
    nextLi.className = `page-item ${currentPage >= totalPages - 1 ? 'disabled' : ''}`;
    nextLi.innerHTML = `<a class="page-link" href="#" ${currentPage < totalPages - 1 ? `onclick="loadTestTypes(${currentPage + 1})"` : ''}>»</a>`;
    pagination.appendChild(nextLi);
}

function addPageItem(container, pageIndex, currentPage) {
    const li = document.createElement('li');
    li.className = `page-item ${pageIndex === currentPage ? 'active' : ''}`;
    li.innerHTML = `<a class="page-link" href="#" onclick="loadTestTypes(${pageIndex})">${pageIndex + 1}</a>`;
    container.appendChild(li);
}

function addEllipsis(container) {
    const li = document.createElement('li');
    li.className = 'page-item disabled';
    li.innerHTML = '<span class="page-link">...</span>';
    container.appendChild(li);
}

function updateSelectedCount() {
    document.getElementById('selectedCount').textContent = selectedTests.size;
}

function updateSelectAllCheckbox() {
    const selectAll = document.getElementById('selectAllTests');
    const checkboxes = document.querySelectorAll('.test-checkbox:not(#selectAllTests)');

    if (checkboxes.length === 0) {
        selectAll.checked = false;
        selectAll.indeterminate = false;
        return;
    }

    const checkedCount = document.querySelectorAll('.test-checkbox:not(#selectAllTests):checked').length;
    selectAll.checked = checkedCount === checkboxes.length && checkedCount > 0;
    selectAll.indeterminate = checkedCount > 0 && checkedCount < checkboxes.length;
}

function updateSelectedTestsDisplay() {
    const selectedTestsContainer = document.getElementById('selectedTestsContainer');
    selectedTestsContainer.innerHTML = selectedTests.size === 0
        ? '<div class="list-group-item text-muted">Không có gì, hãy chọn test</div>'
        : Array.from(selectedTests).map(([id, name]) => `
            <div class="list-group-item d-flex justify-content-between align-items-center">
                <div>
                    <span class="fw-semibold">${name}</span>
                    <small class="text-muted ms-2">ID: ${id}</small>
                </div>
                <button type="button" class="btn btn-sm btn-outline-danger" onclick="removeSelectedTest('${id}')">
                    <i class="fas fa-times"></i>
                </button>
            </div>
        `).join('');
}

function removeSelectedTest(testId) {
    selectedTests.delete(testId);
    updateSelectedCount();
    updateSelectedTestsDisplay();
    updateSelectAllCheckbox();
    const checkbox = document.querySelector(`.test-checkbox[data-test-id="${testId}"]`);
    if (checkbox) checkbox.checked = false;
}

function submitTestRequests() {
    const testContainer = document.getElementById('testContainer');
    const selectedTestIds = Array.from(selectedTests.keys());

    console.log(selectedTestIds)

    if (selectedTestIds.length === 0) {
        alert('Vui lòng chọn ít nhất một xét nghiệm');
        return;
    }

    const testListRequest = {
        appointmentId: appointmentId,
        testTypeId: selectedTestIds
    };

    // Show loading state
    testContainer.innerHTML = '<div class="list-group-item text-muted">Đang gửi yêu cầu...</div>';
    console.log(testListRequest)
    // Make API call
    fetch('/api/test-request/add', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(testListRequest)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to add test requests');
            }
            return response.json();
        })
        .then(data => {
            loadTestRequests();
            const modal = bootstrap.Modal.getInstance(document.getElementById('testModal'));
            modal.hide();
            showToast('success',"Đã yêu cầu test thành công ");
            selectedTests.clear();
            updateSelectedCount();
            updateSelectedTestsDisplay();
        })
        .catch(error => {
            console.error('Error:', error);
            testContainer.innerHTML = '<div class="list-group-item text-danger">Đã xảy ra lỗi khi gửi yêu cầu</div>';
        });
}

function renderTestRequests(testRequests) {
    const testContainer = document.getElementById('testContainer');

    if (testRequests.length === 0) {
        testContainer.innerHTML = '<div class="list-group-item text-muted">Chưa có xét nghiệm nào</div>';
        return;
    }

    testContainer.innerHTML = `
        ${testRequests.map(request => `
            <div class="list-group-item d-flex justify-content-between align-items-center">
                <div>
                    <span class="fw-semibold">${request.testTypeEntityTestTypeName}</span>
                    <small class="text-muted ms-2">ID: ${request.id}</small>
                    <small class="text-muted ms-2">Thời gian: ${new Date(request.requestTime).toLocaleDateString('vi-VN')}</small>
                </div>
                <button type="button" class="btn btn-sm btn-outline-danger" onclick="removeTestRequest(${request.id})">
                    <i class="fas fa-trash"></i>
                </button>
            </div>
        `).join('')}
        <div class="list-group-item d-flex justify-content-end">
            <a href="/" class="btn btn-primary">
                <i class="fas fa-home me-1"></i> Về trang chủ
            </a>
        </div>
    `;
}


function loadTestRequests() {
    const testContainer = document.getElementById('testContainer');
    testContainer.innerHTML = '<div class="list-group-item text-muted">Đang tải danh sách xét nghiệm...</div>';
    console.log(appointmentId)
    fetch(`/api/test-request/${appointmentId}`)
        .then(response => response.json())
        .then(data => {
            renderTestRequests(data);
        })
        .catch(error => {
            console.error('Error:', error);
            testContainer.innerHTML = '<div class="list-group-item text-danger">Đã xảy ra lỗi khi tải danh sách</div>';
        });
}

function removeTestRequest(testRequestId){
    if (confirm('Bạn có chắc muốn xóa xét nghiệm này?')) {
        fetch(`/api/test-request/${testRequestId}/delete`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to delete test request');
                }
                loadTestRequests();
                showLoading();
                showToast('success',"Xóa yêu cầu test thành công")
                hideLoading();
            })
            .catch(error => {
                console.error('Error:', error);
                showLoading();
                showToast('error',"Xóa yêu cầu test thất bại")
                hideLoading();
            });
    }
}
