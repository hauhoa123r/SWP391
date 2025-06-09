package org.project.service;

import org.project.enums.ProductSortType;
import org.project.model.response.PharmacyListResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

public interface PharmacyService {
    List<PharmacyListResponse> getAllPharmacies();
    List<PharmacyListResponse> searchByName(String name);
    List<PharmacyListResponse> searchByCategory(Long categoryId);
    List<PharmacyListResponse> searchByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    List<PharmacyListResponse> searchByType(String type);
    List<PharmacyListResponse> searchByTag(String tagName);
    List<PharmacyListResponse> advancedSearch(String name, String type, String tagName, BigDecimal minPrice, BigDecimal maxPrice);
    List<PharmacyListResponse> sortProducts(List<PharmacyListResponse> products, ProductSortType sortType);
    
}
