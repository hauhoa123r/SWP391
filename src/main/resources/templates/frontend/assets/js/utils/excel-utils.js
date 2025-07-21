export class ExcelUtils {
    static exportJsonToExcel(jsonData, fileName = "report.xlsx") {
        const worksheet = XLSX.utils.json_to_sheet(jsonData);
        const workbook = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(workbook, worksheet, "Report");

        XLSX.writeFile(workbook, fileName);
    }
}