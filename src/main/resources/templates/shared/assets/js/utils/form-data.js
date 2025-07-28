export class FormDataUtils {
    /**
     * Converts a FormData object to a plain object, hỗ trợ nested field.
     * @param {InstanceType} instance - Object mẫu để điền giá trị vào.
     * @param {FormData} formData - FormData để chuyển đổi.
     * @returns {InstanceType} - Object kết quả, có nested field nếu key dạng a.b.c.
     */
    static getObjectFromFormData(instance, formData) {
        // Nếu instance null thì tạo object rỗng
        if (instance === null || instance === undefined) {
            instance = {};
        }
        for (const [key, value] of formData.entries()) {
            const parts = key.split(".");
            let current = instance;
            for (let i = 0; i < parts.length - 1; i++) {
                if (!current[parts[i]]) {
                    current[parts[i]] = {};
                }
                current = current[parts[i]];
            }
            const lastKey = parts[parts.length - 1];
            if (Array.isArray(value)) {
                current[lastKey] = value.map(item => item.trim());
            } else if (typeof value === "string") {
                current[lastKey] = value.trim();
            } else {
                current[lastKey] = value;
            }
        }
        return instance;
    }
}