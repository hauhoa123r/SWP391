package org.project.converter;

import org.project.config.ModelMapperConfig;
import org.project.entity.CategoryEntity;
import org.project.model.response.CategoryListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConverterCategoryProductList {

    @Autowired
    private ModelMapperConfig model;

    public List<CategoryListResponse> toConverterCategoryProductList(List<CategoryEntity> categoryEntities) {
        return categoryEntities.stream()
                .map(categoryEntity -> {
                    CategoryListResponse response = model.mapper().map(categoryEntity, CategoryListResponse.class);
                    return response;
                })
                .toList();
    }
}
