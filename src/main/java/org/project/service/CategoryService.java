package org.project.service;

import org.project.model.response.CategoryListResponse;

import java.util.List;

public interface CategoryService {
    int countProductsByCategory(Long categoryId);

    List<CategoryListResponse> findAllCategory();
}
