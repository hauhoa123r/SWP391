export class Base64Utils {
    /**
     * Converts a file to a Base64 string.
     * @param {File} file - The file to convert.
     * @returns {Promise<object>} A promise that resolves to the Base64 string of the file.
     */
    static async getBase64(file) {
        if (!file || file.size === 0 || !file.name) return null;
        return new Promise(resolve => {
            const reader = new FileReader();
            reader.onload = () =>
                    resolve(reader.result);
            reader.readAsDataURL(file);
        });
    }
}