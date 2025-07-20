package org.project.service.impl;

import lombok.RequiredArgsConstructor;
import org.project.converter.ConverterCategoryProductList;
import org.project.entity.CategoryEntity;
import org.project.model.response.CategoryListResponse;
import org.project.repository.CategoryRepository;
import org.project.repository.ProductCategoryRepository;
import org.project.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ConverterCategoryProductList converterCategoryProductList;

    @Override
    public int countProductsByCategory(Long categoryId) {
        return productCategoryRepository.countByCategoryId(categoryId);
    }

    @Override
    public List<CategoryListResponse> findAllCategory() {
        List<CategoryEntity> categoryEntities = categoryRepository.findAllCategory();
        return converterCategoryProductList.toConverterCategoryProductList(categoryEntities);
    }
}
