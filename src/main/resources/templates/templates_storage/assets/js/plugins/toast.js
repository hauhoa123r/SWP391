/**
 * Toast.js - Thư viện hiển thị thông báo toast đơn giản
 */
class Toast {
    constructor() {
        // Thêm CSS vào trang
        this.injectStyles();

        this.defaultOptions = {
            type: "primary", // primary, success, warning, danger, info
            message: "Đây là thông báo!",
            duration: 5000, // thời gian hiển thị (ms)
            position: "top-right", // top-right, top-left, bottom-right, bottom-left
            dismissible: true, // có thể đóng hay không
            icon: false, // hiển thị icon hay không
            solid: false, // kiểu solid hay không
            rounded: true, // bo góc hay không
            link: null, // liên kết (nếu có)
            heading: null, // tiêu đề (nếu có)
            additionalText: null, // văn bản bổ sung (nếu có)
        };

        this.icons = {
            primary: `<svg class="bi flex-shrink-0 me-2" width="24" height="24"><use xlink:href="#info-fill" /></svg>`,
            success: `<svg class="bi flex-shrink-0 me-2" width="24" height="24"><use xlink:href="#check-circle-fill" /></svg>`,
            warning: `<svg class="bi flex-shrink-0 me-2" width="24" height="24"><use xlink:href="#exclamation-triangle-fill" /></svg>`,
            danger: `<svg class="bi flex-shrink-0 me-2" width="24" height="24"><use xlink:href="#exclamation-triangle-fill" /></svg>`,
            info: `<svg class="bi flex-shrink-0 me-2" width="24" height="24"><use xlink:href="#info-fill" /></svg>`,
        };

        // Thêm SVG icons vào trang
        this.initIcons();

        // Tạo container nếu chưa tồn tại
        this.createContainer();
    }

    // Thêm CSS vào trang
    injectStyles() {
        const css = `
      @keyframes slideInRight {
        from {
          transform: translateX(100%);
          opacity: 0;
        }
        to {
          transform: translateX(0);
          opacity: 1;
        }
      }

      @keyframes slideInLeft {
        from {
          transform: translateX(-100%);
          opacity: 0;
        }
        to {
          transform: translateX(0);
          opacity: 1;
        }
      }

      @keyframes slideInUp {
        from {
          transform: translateY(100%);
          opacity: 0;
        }
        to {
          transform: translateY(0);
          opacity: 1;
        }
      }

      @keyframes slideInDown {
        from {
          transform: translateY(-100%);
          opacity: 0;
        }
        to {
          transform: translateY(0);
          opacity: 1;
        }
      }

      .toast-container-top-right {
        position: fixed;
        top: 20px;
        right: 20px;
        z-index: 9999;
        max-width: 350px;
      }

      .toast-container-top-left {
        position: fixed;
        top: 20px;
        left: 20px;
        z-index: 9999;
        max-width: 350px;
      }

      .toast-container-bottom-right {
        position: fixed;
        bottom: 20px;
        right: 20px;
        z-index: 9999;
        max-width: 350px;
      }

      .toast-container-bottom-left {
        position: fixed;
        bottom: 20px;
        left: 20px;
        z-index: 9999;
        max-width: 350px;
      }

      /* Hiệu ứng cho alert */
      .alert {
        margin-bottom: 15px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        transition: all 0.3s ease-in-out;
        opacity: 0;
      }

      /* Thêm animation cho từng vị trí */
      .toast-container-top-right .alert.show {
        opacity: 1;
        animation: slideInRight 0.5s;
      }

      .toast-container-top-left .alert.show {
        opacity: 1;
        animation: slideInLeft 0.5s;
      }

      .toast-container-bottom-right .alert.show {
        opacity: 1;
        animation: slideInRight 0.5s;
      }

      .toast-container-bottom-left .alert.show {
        opacity: 1;
        animation: slideInLeft 0.5s;
      }

      .alert.fade {
        transition: opacity 0.3s linear;
      }

      /* Khi ẩn toast */
      .alert.fade:not(.show) {
        opacity: 0;
        transform: translateX(100%);
        transition: opacity 0.3s, transform 0.5s;
      }

      /* Kiểu cho alert với đường viền bên trái */
      .alert-left {
        border-left: 5px solid;
      }

      /* Kiểu cho alert với đường viền bên phải */
      .alert-right {
        border-right: 5px solid;
      }

      /* Kiểu cho alert với đường viền bên trên */
      .alert-top {
        border-top: 5px solid;
      }

      /* Kiểu cho alert với đường viền bên dưới */
      .alert-bottom {
        border-bottom: 5px solid;
      }

      /* Kiểu solid */
      .alert-solid {
        color: white;
      }

      .alert-solid.alert-primary {
        background-color: #0d6efd;
        border-color: #0d6efd;
      }

      .alert-solid.alert-success {
        background-color: #198754;
        border-color: #198754;
      }

      .alert-solid.alert-warning {
        background-color: #ffc107;
        border-color: #ffc107;
      }

      .alert-solid.alert-danger {
        background-color: #dc3545;
        border-color: #dc3545;
      }

      .alert-solid.alert-info {
        background-color: #0dcaf0;
        border-color: #0dcaf0;
      }
    `;

        // Tạo một thẻ style
        const style = document.createElement("style");
        style.id = "toast-js-styles";
        style.textContent = css;

        // Thêm vào head nếu chưa tồn tại
        if (!document.getElementById("toast-js-styles")) {
            document.head.appendChild(style);
        }
    }

    // Còn lại giữ nguyên code như cũ
    // ...

    // Khởi tạo SVG icons
    initIcons() {
        if (!document.getElementById("toast-icons")) {
            const iconsSvg = document.createElement("div");
            iconsSvg.style.display = "none";
            iconsSvg.id = "toast-icons";
            iconsSvg.innerHTML = `
        <svg xmlns="http://www.w3.org/2000/svg" style="display: none;">
          <symbol id="check-circle-fill" fill="currentColor" viewBox="0 0 16 16">
            <path d="M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0zm-3.97-3.03a.75.75 0 0 0-1.08.022L7.477 9.417 5.384 7.323a.75.75 0 0 0-1.06 1.06L6.97 11.03a.75.75 0 0 0 1.079-.02l3.992-4.99a.75.75 0 0 0-.01-1.05z" />
          </symbol>
          <symbol id="info-fill" fill="currentColor" viewBox="0 0 16 16">
            <path d="M8 16A8 8 0 1 0 8 0a8 8 0 0 0 0 16zm.93-9.412-1 4.705c-.07.34.029.533.304.533.194 0 .487-.07.686-.246l-.088.416c-.287.346-.92.598-1.465.598-.703 0-1.002-.422-.808-1.319l.738-3.468c.064-.293.006-.399-.287-.47l-.451-.081.082-.381 2.29-.287zM8 5.5a1 1 0 1 1 0-2 1 1 0 0 1 0 2z" />
          </symbol>
          <symbol id="exclamation-triangle-fill" fill="currentColor" viewBox="0 0 16 16">
            <path d="M8.982 1.566a1.13 1.13 0 0 0-1.96 0L.165 13.233c-.457.778.091 1.767.98 1.767h13.713c.889 0 1.438-.99.98-1.767L8.982 1.566zM8 5c.535 0 .954.462.9.995l-.35 3.507a.552.552 0 0 1-1.1 0L7.1 5.995A.905.905 0 0 1 8 5zm.002 6a1 1 0 1 1 0 2 1 1 0 0 1 0-2z" />
          </symbol>
        </svg>
      `;
            document.body.appendChild(iconsSvg);
        }
    }

    // Tạo container cho các toast
    createContainer() {
        const positions = [
            "top-right",
            "top-left",
            "bottom-right",
            "bottom-left",
        ];

        positions.forEach((position) => {
            if (!document.querySelector(`.toast-container-${position}`)) {
                const container = document.createElement("div");
                container.className = `toast-container-${position}`;
                document.body.appendChild(container);
            }
        });
    }

    // Hiển thị toast
    show(options = {}) {
        const settings = {...this.defaultOptions, ...options};

        // Tạo toast element
        const toastElement = document.createElement("div");

        // Xác định vị trí container
        let positionClass = "top-right";
        if (settings.position === "top-left") positionClass = "top-left";
        if (settings.position === "bottom-right")
            positionClass = "bottom-right";
        if (settings.position === "bottom-left") positionClass = "bottom-left";

        // Các lớp CSS cho vị trí
        let positionStyleClass = "";
        if (settings.position.includes("top")) positionStyleClass = "alert-top";
        else if (settings.position.includes("bottom"))
            positionStyleClass = "alert-bottom";

        if (settings.position.includes("left"))
            positionStyleClass = "alert-left";
        else if (settings.position.includes("right"))
            positionStyleClass = "alert-right";

        // Tạo các lớp CSS cho toast
        const classes = [
            "alert",
            `alert-${settings.type}`,
            "alert-dismissible",
            "fade",
            "show",
        ];

        // Thêm các lớp tùy chọn
        if (!settings.rounded) classes.push("rounded-0");
        if (settings.solid) classes.push("alert-solid");
        if (positionStyleClass) classes.push(positionStyleClass);
        if (settings.icon) classes.push("d-flex", "align-items-center");

        toastElement.className = classes.join(" ");
        toastElement.setAttribute("role", "alert");

        // Tạo nội dung HTML cho toast
        let contentHTML = "";

        // Thêm tiêu đề nếu có
        if (settings.heading) {
            contentHTML += `<h4 class="alert-heading">${settings.heading}</h4>`;
        }

        // Thêm icon nếu cần
        if (settings.icon) {
            contentHTML += this.icons[settings.type];
            contentHTML += `<div>`;
        }

        // Thêm nội dung chính
        if (settings.link) {
            contentHTML += `${settings.message} <a href="${settings.link}" class="alert-link">nhấp vào đây</a>`;
        } else {
            contentHTML += `<span>${settings.message}</span>`;
        }

        // Đóng thẻ div nếu có icon
        if (settings.icon) {
            contentHTML += `</div>`;
        }

        // Thêm văn bản bổ sung nếu có
        if (settings.additionalText) {
            contentHTML += `<hr><p class="mb-0">${settings.additionalText}</p>`;
        }

        // Thêm nút đóng nếu có thể đóng
        if (settings.dismissible) {
            contentHTML += `<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Đóng"></button>`;
        }

        toastElement.innerHTML = contentHTML;

        // Thêm toast vào container
        const container = document.querySelector(
            `.toast-container-${positionClass}`
        );
        container.appendChild(toastElement);

        // Thêm sự kiện cho nút đóng
        const closeButton = toastElement.querySelector(".btn-close");
        if (closeButton) {
            closeButton.addEventListener("click", () => {
                this.hide(toastElement);
            });
        }

        // Tự động đóng sau thời gian cài đặt
        if (settings.duration > 0) {
            setTimeout(() => {
                this.hide(toastElement);
            }, settings.duration);
        }

        return toastElement;
    }

    // Ẩn toast
    hide(toastElement) {
        if (toastElement && toastElement.parentNode) {
            toastElement.classList.remove("show");
            setTimeout(() => {
                if (toastElement.parentNode) {
                    toastElement.parentNode.removeChild(toastElement);
                }
            }, 300); // Đợi hiệu ứng fade out hoàn thành
        }
    }

    // Phương thức nhanh cho các loại toast khác nhau
    primary(message, options = {}) {
        return this.show({...options, type: "primary", message});
    }

    success(message, options = {}) {
        return this.show({...options, type: "success", message});
    }

    warning(message, options = {}) {
        return this.show({...options, type: "warning", message});
    }

    danger(message, options = {}) {
        return this.show({...options, type: "danger", message});
    }

    info(message, options = {}) {
        return this.show({...options, type: "info", message});
    }
}

// Khởi tạo toast global
export default new Toast();
