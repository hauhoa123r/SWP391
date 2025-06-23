package org.project.service;

import org.project.enums.ProductSortType;
import org.project.model.response.PharmacyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    Page<PharmacyResponse> searchProducts(Optional<String> searchQuery, Optional<Long> categoryId,
                                              Optional<BigDecimal> minPrice, Optional<BigDecimal> maxPrice,
                                              Optional<String> type, ProductSortType sortType, Pageable pageable);

//    PharmacyListResponse findById(Long id);
}