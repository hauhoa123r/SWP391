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

        return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}`;
    }

    toHtml() {
        return `
        <div class="col-md-4 col-sm-6 time-slot time">
            <input style="display: none" id="date-time-${this.availableTimes}" type="radio" name="time-slot" value="${this.availableTimes}">
            <button
                    class="btn bg-white text-body text-uppercase p-0 w-100 time-slot" data-time="${this.availableTimes}">
                <label for="date-time-${this.availableTimes}" class="w-100 h-100 p-2">
                    ${this.formatTime()}
                </label>
            </button>
        </div>
        `;
    }
}