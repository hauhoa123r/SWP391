export class SearchParamsUtils {
    static toSearchParams(obj, prefix = "") {
        if (obj === null || obj === undefined) {
            return new URLSearchParams();
        }
        const searchParams = new URLSearchParams();

        function appendParams(o, p = "") {
            for (const [key, value] of Object.entries(o)) {
                const paramKey = p ? `${p}.${key}` : key;
                if (Array.isArray(value)) {
                    value.forEach(item => {
                        if (typeof item === "object" && item !== null) {
                            appendParams(item, paramKey);
                        } else {
                            searchParams.append(paramKey, item);
                        }
                    });
                } else if (typeof value === "object" && value !== null) {
                    appendParams(value, paramKey);
                } else if (value !== undefined && value !== null && value !== "") {
                    searchParams.set(paramKey, value);
                }
            }
        }

        appendParams(obj, prefix);
        return searchParams;
    }
}