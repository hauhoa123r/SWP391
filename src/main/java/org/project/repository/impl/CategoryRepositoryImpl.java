package org.project.repository.impl;

import org.project.entity.CategoryEntity;
import org.project.repository.CategoryRepository;

import java.util.List;

public abstract class CategoryRepositoryImpl implements CategoryRepository {

    @Override
    public List<CategoryEntity> findAllCategory() {
        return List.of();
    }

}
