// Global JavaScript utilities

// Loading overlay functions
function showLoading() {
    const loadingEl = document.getElementById('loadingOverlay');
    if (loadingEl) loadingEl.classList.add('show');
}

function hideLoading() {
    const loadingEl = document.getElementById('loadingOverlay');
    if (loadingEl) loadingEl.classList.remove('show');
}

function getToastIcon(type) {
    const icons = {
        success: 'bi-check-circle',
        error: 'bi-exclamation-circle',
        warning: 'bi-exclamation-triangle',
        info: 'bi-info-circle'
    };
    return icons[type] || 'bi-info-circle';
}

function getToastTitle(type) {
    const titles = {
        success: 'Success',
        error: 'Error',
        warning: 'Warning',
        info: 'Information'
    };
    return titles[type] || 'Notification';
}

let toastInstance = null;

function showToast(type, message) {
    const toastElement = document.getElementById('liveToast');
    const toastBody = document.getElementById('toastBody');
    const toastIcon = document.getElementById('toastIcon');
    const toastTitle = document.getElementById('toastTitle');

    // Định nghĩa icon bootstrap icon và tiêu đề theo loại
    const icons = {
        success: 'bi-check-circle',
        error: 'bi-exclamation-circle',
        warning: 'bi-exclamation-triangle',
        info: 'bi-info-circle'
    };

    const titles = {
        success: 'Success',
        error: 'Error',
        warning: 'Warning',
        info: 'Information'
    };

    // Chuẩn hóa 'type'
    type = (typeof type === 'string') ? type.trim().toLowerCase() : 'info';

    // Cập nhật icon, tiêu đề, message
    toastIcon.className = `bi ${icons[type] || icons.info} me-2`;
    toastTitle.textContent = titles[type] || titles.info;
    toastBody.textContent = message;

    // Loại bỏ các lớp màu cũ rồi thêm lớp màu mới
    toastElement.classList.remove('bg-success', 'bg-danger', 'bg-warning', 'bg-info', 'text-white');

    // Gán màu nền và chữ cho toast tùy loại
    switch(type) {
        case 'success':
            toastElement.classList.add('bg-success', 'text-white');
            break;
        case 'error':
            toastElement.classList.add('bg-danger', 'text-white');
            break;
        case 'warning':
            toastElement.classList.add('bg-warning', 'text-white');
            break;
        case 'info':
        default:
            toastElement.classList.add('bg-info', 'text-white');
            break;
    }

    // Khởi tạo và hiển thị toast
    const toast = new bootstrap.Toast(toastElement, { delay: 4000 });
    toast.show();
}

// Global error handler
window.addEventListener('error', function (e) {
    console.error('Global error:', e.error);
    hideLoading();
    showToast('An unexpected error occurred. Please try again.', 'error');
});

// Global unhandled promise rejection handler
window.addEventListener('unhandledrejection', function (e) {
    console.error('Unhandled promise rejection:', e.reason);
    hideLoading();
    showToast('A network error occurred. Please check your connection.', 'error');
});

// Page load complete
document.addEventListener('DOMContentLoaded', function () {
    setTimeout(() => {
        hideLoading();
    }, 500);

    // Initialize Bootstrap tooltips and popovers
    let tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (el) {
        return new bootstrap.Tooltip(el);
    });

    let popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    popoverTriggerList.map(function (el) {
        return new bootstrap.Popover(el);
    });

    console.log('Doctor Portal loaded successfully');
});

// Fire custom event when everything is loaded
window.addEventListener('load', function () {
    const loadedEvent = new CustomEvent('portal:loaded');
    document.dispatchEvent(loadedEvent);
});
