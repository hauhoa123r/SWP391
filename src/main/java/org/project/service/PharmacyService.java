package org.project.service;


import org.project.model.response.PharmacyListResponse;
import org.springframework.stereotype.Service;

import java.util.List;


public interface PharmacyService {
    List<PharmacyListResponse> getAllPharmacies();
    List<PharmacyListResponse> searchByName(String name);
    List<PharmacyListResponse> searchByPriceRange(java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice);
    List<PharmacyListResponse> searchByType(String type);
    List<PharmacyListResponse> advancedSearch(String name, String type, java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice);
}
