export class FormDataUtils {
    /**
     * Converts a FormData object to a plain object.
     * @param {InstanceType} instance - The object to populate with FormData values.
     * @param {FormData} formData - The FormData object to convert.
     * @returns {Object} - The converted plain object.
     */
    static getObjectFromFormData(instance, formData) {
        Object.keys(instance).forEach(key => {
            const value = formData.has(key) ? formData.get(key) : undefined;
            if (value !== undefined && value !== null) {
                if (Array.isArray(value)) {
                    instance[key] = value.map(item => item.trim());
                } else if (typeof value === "string") {
                    instance[key] = value.trim();
                } else {
                    instance[key] = value;
                }
            }
        });
        return instance;
    }
}