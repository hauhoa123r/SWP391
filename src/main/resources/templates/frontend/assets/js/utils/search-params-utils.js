export class SearchParamsUtils {
    static toSearchParams(obj) {
        const searchParams = new URLSearchParams();
        for (const [key, value] of Object.entries(obj)) {
            if (Array.isArray(value)) {
                value.forEach(item => searchParams.append(key, item));
            } else if (value !== undefined && value !== null && value !== "") {
                searchParams.set(key, value);
            }
        }
        return searchParams;
    }
}