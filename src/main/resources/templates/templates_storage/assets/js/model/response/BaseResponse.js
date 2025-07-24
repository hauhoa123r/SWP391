export class BaseResponse {
    static fromJson(json) {
        throw new Error("Method 'fromJson()' must be implemented.");
    }

    static fromJsonArray(jsonArray) {
        throw new Error("Method 'fromJsonArray()' must be implemented.");
    }

    toHtml() {
        throw new Error("Method 'toHtml()' must be implemented.");
    }
}
