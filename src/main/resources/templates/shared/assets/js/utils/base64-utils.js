export class Base64Utils {
    /**
     * Converts a file to a Base64 string.
     * @param {File} file - The file to convert.
     * @returns {Promise<string>} A promise that resolves to the Base64 string of the file.
     */
    static async getBase64(file) {
        return new Promise(resolve => {
            const reader = new FileReader();
            reader.onload = () =>
                    resolve(reader.result);
            reader.readAsDataURL(file);
        });
    }
}