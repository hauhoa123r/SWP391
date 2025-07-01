import toast from "/templates/frontend/assets/js/plugins/toast.js";

export async function fetchData(url) {
    try {
        const response = await fetch(url);
        if (!response.ok) {
            const errorMessage = await response.text();
            toast.danger(errorMessage, {
                duration: 3000,
                position: "top-right",
                icon: true
            });
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error("Fetch error:", error);
        return null;
    }
}