document.addEventListener("DOMContentLoaded", function () {

    let currentPage = 1;
    let totalPages = 1;
    let itemsPerPage = 6;

    let currentSearch = '';
    let currentStatus = 'CONFIRMED';
    let currentDateFilter = 'today';
    let currentSpecificDate = '';

    const select = document.getElementById('itemsPerPageSelect');
    const customInput = document.getElementById('customItemsInput');

    itemsPerPage = parseInt(select.value) || itemsPerPage;

    select.addEventListener('change', () => {
        if (select.value === 'custom') {
            customInput.style.display = 'inline-block';
            customInput.focus();
        } else {
            customInput.style.display = 'none';
            const value = parseInt(select.value);
            if (!isNaN(value)) {
                itemsPerPage = value;
                currentPage = 1;
                fetchAppointments(currentPage);
                console.log('Số phần tử mỗi trang:', itemsPerPage);
            }
        }
    });

    customInput.addEventListener('input', () => {
        const val = parseInt(customInput.value);
        if (!isNaN(val) && val > 0) {
            itemsPerPage = val;
            currentPage = 1;
            fetchAppointments(currentPage);
        }
    });
    let currentStartDate = null;
    let currentEndDate = null;

    document.getElementById('dateFilter').addEventListener('change', function(e) {
        currentDateFilter = e.target.value;
        document.getElementById('customDateRangeContainer').style.display =
            currentDateFilter === 'custom' ? 'block' : 'none';
        if (currentDateFilter !== 'custom') {
            currentStartDate = null;
            currentEndDate = null;
        }

        currentPage = 1;
        fetchAppointments(currentPage);
    });

    function validateDateRange(startDate, endDate) {
        if (startDate && endDate) {
            const start = new Date(startDate);
            const end = new Date(endDate);

            if (start > end) {
                showToast('error', 'Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc');
                console.log(`Validation failed: ${startDate} > ${endDate}`);
                return false;
            }
        }
        return true;
    }

    document.getElementById('startDate').addEventListener('change', function(e) {
        currentStartDate = e.target.value;
        currentPage = 1;
        if (validateDateRange(currentStartDate, currentEndDate)) {
            fetchAppointments(currentPage);
        }
    });

    document.getElementById('endDate').addEventListener('change', function(e) {
        currentEndDate = e.target.value;
        currentPage = 1;
        if (validateDateRange(currentStartDate, currentEndDate)) {
            fetchAppointments(currentPage);
        }
    });
    function fetchAppointments(page = 1) {
        const filterRequest = {
            search: currentSearch,
            status: currentStatus,
            dateFilter: currentDateFilter,
            startDate: currentStartDate,
            endDate: currentEndDate,
            sortTime: document.getElementById('sortTime').value
        };
        console.log(filterRequest)
        const params = new URLSearchParams({
            page: page - 1,
            size: itemsPerPage
        });
        Object.entries(filterRequest).forEach(([key, value]) => {
            if (value !== null && value !== undefined && value !== '') {
                params.append(key, value);
            }
        });
        console.log(params.toString())
        fetch(`/api/appointments/${doctor_id}?${params.toString()}`, {
        })
            .then(response => {
                if (!response.ok) throw new Error("Failed to fetch appointments");
                return response.json();
            })
            .then(data => {
                displayAppointments(data.content);
                updatePagination(data.totalPages, page);
                totalPages = data.totalPages;
            })
            .catch(error => {
                console.error("Error loading appointments:", error);
            });
    }

    function displayAppointments(appointments) {
        const appointmentsList = document.getElementById("appointmentsList");
        const emptyState = document.getElementById("emptyState");

        if (!appointments || appointments.length === 0) {
            appointmentsList.innerHTML = "";
            emptyState.style.display = "block";
            return;
        }
        emptyState.style.display = "none";
        appointmentsList.innerHTML = appointments.map(appointment => createAppointmentCard(appointment)).join("");
    }

    function createAppointmentCard(appointment) {
        const statusBadgeClass = getStatusBadgeClass(appointment.appointmentStatus);
        const actionButtons = getActionButtons(appointment);

        return `
        <div class="col-lg-6 col-xl-4 mb-4">
            <div class="card appointment-card" data-appointment-id="${appointment.id}">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-start mb-3">
                        <div class="d-flex align-items-center">
                            <img src="${appointment.patientEntityAvatarUrl || 'https://via.placeholder.com/50x50/667eea/white?text=U'}"
                                 alt="Patient" class="patient-avatar me-3">
                            <div>
                                <h6 class="mb-1 fw-bold">${appointment.patientEntityName}</h6>
                            </div>
                        </div>
                        <span class="status-badge ${statusBadgeClass}">${appointment.appointmentStatus}</span>
                    </div>

                    <div class="row mb-3">
    <div class="col-12 mb-2">
        <span class="time-slot">
            <i class="fas fa-clock me-1"></i>
            ${formatDateTime(appointment.startTime)} (${appointment.durationMinutes}min)
        </span>
    </div>

    <div class="col-12 mb-2">
        <div class="d-flex align-items-center text-muted">
            <i class="fas fa-phone me-2"></i>
            <span>${appointment.patientEntityPhoneNumber}</span>
        </div>
    </div>

    <div class="col-12 mb-2">
        <div class="d-flex align-items-center text-muted">
            <i class="fas fa-stethoscope me-2"></i>
            <span>${appointment.serviceEntityProductEntityName}</span>
        </div>
    </div>

    <div class="col-12 mb-2">
        <div class="d-flex align-items-center text-muted">
            <i class="fas fa-user-tie me-2"></i>
            <span>Coordinator: ${appointment.schedulingCoordinatorEntityName}</span>
        </div>
    </div>
</div>



                    <div class="d-flex gap-2">
                        ${actionButtons}
                    </div>
                </div>
            </div>
        </div>
    `;
    }

    function formatDateTime(ts) {
        if (!ts) return '';
        const date = new Date(ts);
        return date.toLocaleString();
    }

    function getStatusBadgeClass(status) {
        switch (status.toLowerCase()) {
            case 'confirmed':
                return 'status-confirmed';
            case 'pending':
                return 'status-pending';
            case 'completed':
                return 'status-completed';
            case 'cancelled':
                return 'status-cancelled';
            default:
                return 'status-pending';
        }
    }

    function getActionButtons(appointment) {
        switch (appointment.appointmentStatus.toLowerCase()) {
            case 'confirmed':
                return `
                <button class="btn btn-warning flex-fill" onclick="startExamination(${appointment.id})">
                    <i class="fas fa-stethoscope me-2"></i>Vào khám
                </button>
                <button class="btn btn-danger" onclick="cancelAppointment(${appointment.id})">
                    <i class="fas fa-times me-2"></i>Reject
                </button>`;
            case 'completed':
                return `
                <button class="btn btn-info flex-fill" onclick="viewResults(${appointment.id})">
                    <i class="fas fa-eye me-2"></i>Xem kết quả
                </button>`;
            default:
                return `<span class="text-muted"><i class="fas fa-ban me-2"></i>Không có hành động</span>`;
        }
    }
    function formatLocalDate(date) {
        if (!date) return null;
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
    function updateStats() {
        const confirmed = allAppointments.filter(a => a.appointmentStatus === 'Confirmed').length;
        const completed = allAppointments.filter(a => a.appointmentStatus === 'Completed').length;

        document.querySelector('.stat-number').textContent = allAppointments.length;
        document.querySelectorAll('.stat-number')[1].textContent = confirmed;
        document.querySelectorAll('.stat-number')[2].textContent = pending;
        document.querySelectorAll('.stat-number')[3].textContent = completed;
    }

    function updatePagination(totalPages, currentPage) {
        const paginationContainer = document.getElementById("paginationContainer");
        const pagination = document.getElementById("pagination");
        paginationContainer.style.display = "block";
        let paginationHTML = "";
        paginationHTML += `
            <li class="page-item ${currentPage === 1 ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="changePage(${currentPage - 1})">Previous</a>
            </li>
        `;
        for (let i = 1; i <= totalPages; i++) {
            paginationHTML += `
                <li class="page-item ${i === currentPage ? 'active' : ''}">
                    <a class="page-link" href="#" onclick="changePage(${i})">${i}</a>
                </li>
            `;
        }
        paginationHTML += `
            <li class="page-item ${currentPage === totalPages ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="changePage(${currentPage + 1})">Next</a>
            </li>
        `;
        pagination.innerHTML = paginationHTML;
    }

    window.changePage = function (page) {
        if (page >= 1 && page <= totalPages) {
            currentPage = page;
            fetchAppointments(currentPage);
        }
    };


    window.cancelAppointment = function (id) {
        if (confirm('Bạn có chắc chắn muốn hủy cuộc hẹn này?')) {
            const data = {
                id: id,
                appointmentStatus: "CANCELLED"
            };

            fetch(`/api/appointments/${id}/status`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data)
            })
                .then(response => {
                    if (!response.ok) throw new Error("Failed to update appointment status");
                    return response.json();
                })
                .then(updatedAppointment => {
                    alert('Đã hủy cuộc hẹn thành công');
                    fetchAppointments(currentPage); // Refresh danh sách
                })
                .catch(error => {
                    console.error("Error:", error);
                    alert('Có lỗi xảy ra khi hủy cuộc hẹn');
                });
        }
    };

    window.startExamination = function (id) {
        const data = {
            id: id,
            appointmentStatus: "IN_PROGRESS"
        };

        fetch(`/api/appointments/${id}/status`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data)
        })
            .then(response => {
                if (!response.ok) throw new Error("Failed to update appointment status");
                return response.json();
            })
            .then(updatedAppointment => {
                window.location.href = `/staff/doctor/in-progress?id=${id}`;
            })
            .catch(error => {
                console.error("Error:", error);
                alert('Có lỗi xảy ra khi bắt đầu khám');
            });
    };

    window.viewResults = function (id) {
        window.location.href = `staff/doctor/results?id=${id}`;
    };


    document.getElementById('searchInput').addEventListener('input', function (e) {
        currentSearch = e.target.value.trim();
        currentPage = 1;
        fetchAppointments(currentPage);
    });

    document.getElementById('statusFilter').addEventListener('change', function (e) {
        currentStatus = e.target.value;
        currentPage = 1;
        fetchAppointments(currentPage);
    });

    document.getElementById('dateFilter').addEventListener('change', function(e) {
        const isCustom = e.target.value === 'custom';
        document.getElementById('customDateRangeContainer').style.display = isCustom ? 'block' : 'none';

        if (isCustom) {
            const today = new Date().toISOString().split('T')[0];
            document.getElementById('startDate').value = today;
            document.getElementById('endDate').value = today;
        }
    });


    window.resetFilters = function() {
        document.getElementById('searchInput').value = '';
        document.getElementById('statusFilter').value = 'CONFIRMED';
        document.getElementById('dateFilter').value = 'today';
        document.getElementById('sortTime').value = '';
        document.getElementById('startDate').value = '';
        document.getElementById('endDate').value = '';
        document.getElementById('customDateRangeContainer').style.display = 'none';

        currentSearch = '';
        currentStatus = 'CONFIRMED';
        currentDateFilter = 'today';
        currentStartDate = null;
        currentEndDate = null;
        currentPage = 1;

        fetchAppointments(currentPage);
    };

    fetchAppointments(currentPage);
});