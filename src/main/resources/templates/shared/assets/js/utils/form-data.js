export class FormDataUtils {
    /**
     * Converts a FormData object to a plain object, hỗ trợ nested field và mảng dạng a[0].b
     * @param {InstanceType} instance - Object mẫu để điền giá trị vào.
     * @param {FormData} formData - FormData để chuyển đổi.
     * @returns {InstanceType} - Object kết quả, có nested field và mảng nếu key dạng a[0].b.
     */
    static getObjectFromFormData(instance, formData) {
        if (instance === null || instance === undefined) instance = {};
        for (const [key, value] of formData.entries()) {
            let trimValue = value;
            if (typeof value === "string") {
                // Nếu là key cuối và giá trị là string, gán giá trị
                if (value.trim() === "") {
                    trimValue = null; // Chuyển chuỗi rỗng thành null
                }
            }
            const parts = key.split(".");
            let current = instance;
            for (let i = 0; i < parts.length; i++) {
                // Kiểm tra có phải mảng không
                const arrMatch = parts[i].match(/^(\w+)\[(\d+)\]$/);
                if (arrMatch) {
                    const arrName = arrMatch[1];
                    const idx = Number(arrMatch[2]);
                    if (!Array.isArray(current[arrName])) current[arrName] = [];
                    if (!current[arrName][idx]) current[arrName][idx] = {};
                    if (i === parts.length - 1) {
                        // Nếu là key cuối, gán giá trị
                        current[arrName][idx] = trimValue;
                    } else {
                        current = current[arrName][idx];
                    }
                } else {
                    if (i === parts.length - 1) {
                        current[parts[i]] = trimValue;
                    } else {
                        if (!current[parts[i]]) current[parts[i]] = {};
                        current = current[parts[i]];
                    }
                }
            }
        }
        return instance;
    }
}