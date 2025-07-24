export class TimeResponse {
    constructor(availableTimes) {
        this.availableTimes = availableTimes;
    }

    static fromJson(json) {
        return new TimeResponse(json);
    }

    static fromJsonArray(jsonArray) {
        return jsonArray.map(json => TimeResponse.fromJson(json));
    }

    formatTime() {
        const date = new Date(this.availableTimes);
        const hours = date.getHours();
        const minutes = date.getMinutes();

        return `${hours.toString().padStart(2, "0")}:${minutes.toString().padStart(2, "0")}`;
    }

    setRenderStrategy(strategy) {
        this.renderStrategy = strategy;
        return this;
    }

    toHtml(...args) {
        if (this.renderStrategy) {
            return this.renderStrategy(this, ...args);
        }
        throw new Error("Render strategy is not set for TimeResponse");
    }
}

/**
 * Renders the time response for booking in HTML format.
 * @param {TimeResponse} timeResponse - The TimeResponse object to render.
 * @returns {string}
 */
export function renderTimeResponseForBooking(timeResponse) {
    return `
        <div class="col-md-4 col-sm-6 time-slot time">
            <input style="display: none" id="date-time-${timeResponse.availableTimes}" type="radio" name="time-slot" value="${timeResponse.availableTimes}">
            <button
                    class="btn bg-white text-body text-uppercase p-0 w-100 time-slot" data-time="${timeResponse.availableTimes}">
                <label for="date-time-${timeResponse.availableTimes}" class="w-100 h-100 p-2">
                    ${timeResponse.formatTime()}
                </label>
            </button>
        </div>
        `;
}