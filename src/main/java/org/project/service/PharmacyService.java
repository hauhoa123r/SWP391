package org.project.service;

import org.project.entity.ProductEntity;
import org.project.enums.ProductSortType;
import org.project.model.response.PharmacyListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

public interface PharmacyService {
    List<PharmacyListResponse> findTop10Products();

    PharmacyListResponse findById(Long id);
//    List<PharmacyListResponse> getAllPharmacies();
//    Page<PharmacyListResponse> getAllProducts(Pageable pageable, ProductSortType sortType);
//    Page<PharmacyListResponse> searchByName(String name, Pageable pageable, ProductSortType sortType);
//    List<PharmacyListResponse> sortProducts(List<PharmacyListResponse> dtos, ProductSortType sortType);
//    Page<PharmacyListResponse> searchByCategory(Long categoryId, Pageable pageable , ProductSortType sortType);
//    Page<PharmacyListResponse> searchByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
//    Page<PharmacyListResponse> searchByType(String type, Pageable pageable);

}
