export class FormatUtils {
    static formatNumber(number, decimalPlaces = 2) {
        if (number === null || number === undefined) return "";
        if (isNaN(number)) return number.toString();
        return parseFloat(number).toFixed(decimalPlaces);
    }

    /**
     * Format a date string to Vietnamese date format
     * @param {string} dateString
     * @returns {string}
     */
    static formatDate(dateString) {
        if (!dateString) return "";
        const date = new Date(dateString);
        return date.toLocaleDateString("vi-VN", {
            year: "numeric",
            month: "2-digit",
            day: "2-digit"
        });
    }

    /**
     * Format a price to VND currency format
     * @param {number} price
     * @returns {string}
     */
    static formatPrice(price) {
        return new Intl.NumberFormat("vi-VN", {
            style: "currency",
            currency: "VND"
        }).format(price);
    }

    /**
     * Format a timestamp to a readable date and time string
     * @param timestamp
     * @returns {string}
     */
    static formatTimeStamp(timestamp) {
        if (!timestamp) return "";
        const date = new Date(timestamp);
        const pad = (n) => n.toString().padStart(2, "0");
        return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
    }
}