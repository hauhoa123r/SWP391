import toast from "/templates/frontend/assets/js/plugins/toast.js";

export class FetchingUtils {
    /**
     * Fetches data from a given URL with specified options and response type.
     * @param {string} url - The URL to fetch data from.
     * @param {options} options - The options for the fetch request, including method and headers.
     * @param {string} responseType - The expected response type, can be "json", "text", or "blob".
     * @returns {Promise<null|any|Blob|string>}
     */
    static async fetch(url, options = {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        }
    }, responseType                 = "json") {
        try {
            const response = await fetch(url, options);
            if (!response.ok) {
                const errorMessage = await response.text();
                toast.danger(errorMessage, {
                    duration: 3000,
                    position: "top-right",
                    icon: true
                });
                console.log(errorMessage);
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            if (responseType === "text") {
                return await response.text();
            }
            if (responseType === "blob") {
                return await response.blob();
            }
            return await response.json();
        } catch (error) {
            console.error("Fetch error:", error);
            return null;
        }
    }
}