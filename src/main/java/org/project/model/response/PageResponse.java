package org.project.model.response;

import lombok.Data;
import org.springframework.data.domain.Page;

@Data
public class PageResponse<T> {
    private Page<T> content;  // Dữ liệu phân trang
    private long totalElements;  // Tổng số phần tử
    private int totalPages;  // Tổng số trang
    private int currentPage;  // Trang hiện tại

    public PageResponse(Page<T> content) {
        this.content = content;
        this.totalElements = content.getTotalElements();
        this.totalPages = content.getTotalPages();
        this.currentPage = content.getNumber() + 1;  // Trang hiện tại (1-based)
    }
}
