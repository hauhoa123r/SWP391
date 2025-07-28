import toast from "/templates/shared/assets/js/utils/toast.js";

export class FetchingUtils {
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