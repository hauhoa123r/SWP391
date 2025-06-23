package org.project.repository.impl;

import org.project.entity.ProductEntity;
import org.project.enums.ProductLabel;
import org.project.model.response.PharmacyResponse;
import org.project.repository.PharmacyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public abstract class PharmacyRepositoryImpl implements PharmacyRepository {
    @Override
    public Page<ProductEntity> findByNameContainingIgnoreCase(String name, Pageable pageable) {
        return new PageImpl<>(List.of(), pageable, 0);
    }

    @Override
    public Page<ProductEntity> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return new PageImpl<>(List.of(), pageable, 0);
    }
    
    @Override
    public Page<ProductEntity> findByCategoryEntities_Id(Long categoryId, Pageable pageable) {
        return new PageImpl<>(List.of(), pageable, 0);
    }

    @Override
    public Page<ProductEntity> findByLabel(ProductLabel label, Pageable pageable) {
        return new PageImpl<>(List.of(), pageable, 0);
    }

    @Override
    public Page<ProductEntity> findByTagName(String tag, Pageable pageable) {
        return null;
    }

    @Override
    public Page<ProductEntity> findByTag(String tagName, Pageable pageable) {
        return new PageImpl<>(List.of(), pageable, 0);
    }

    @Override
    public List<ProductEntity> findAllWithCategory() {
        return List.of();
    }

    @Override
    public Long findFirstProductId() {
        return 0L;
    }

    @Override
    public Optional<ProductEntity> findById(Long id) {
        return Optional.empty();
    }


//    @Override
//    public List<ProductEntity> advancedSearch(String name, ProductLabel label, String tagName, BigDecimal minPrice, BigDecimal maxPrice) {
//        return List.of();
//    }

//    @Override
//    public Optional<ProductEntity> findByIdWithDetails(Long id) {
//        return Optional.empty();
//    }
//
//    @Override
//    public Integer checkInventoryQuantity(Long productId) {
//        return 0;
//    }
}
