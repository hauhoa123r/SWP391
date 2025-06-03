package org.project.service.impl;

import lombok.RequiredArgsConstructor;
import org.project.converter.ConverterPharmacyProduct;
import org.project.entity.PharmacyProductEntity;
import org.project.model.response.PharmacyListResponse;
import org.project.repository.PharmacyRepository;
import org.project.service.PharmacyService;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PharmacyServiceImpl implements PharmacyService {
    private final PharmacyRepository pharmacyProductRepository;
    private final ConverterPharmacyProduct toConverterPharmacy;

    @Override
    public List<PharmacyListResponse> getAllPharmacies() {
        return toConverterPharmacy.toConverterPharmacyProductList(pharmacyProductRepository.findAll());
    }

    @Override
    public List<PharmacyListResponse> searchByName(String name) {
        String normalized = normalize(name);
        if (normalized == null) return getAllPharmacies();
        return toConverterPharmacy.toConverterPharmacyProductList(
                pharmacyProductRepository.findByNameContainingIgnoreCase(normalized)
        );
    }

    @Override
    public List<PharmacyListResponse> searchByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        minPrice = safeMin(minPrice);
        maxPrice = safeMax(maxPrice);
        validatePriceRange(minPrice, maxPrice);
        return toConverterPharmacy.toConverterPharmacyProductList(
                pharmacyProductRepository.findByPriceBetween(minPrice, maxPrice)
        );
    }

    @Override
    public List<PharmacyListResponse> searchByType(String type) {
        String normalized = normalize(type);
        if (normalized == null) return getAllPharmacies();
        return toConverterPharmacy.toConverterPharmacyProductList(
                pharmacyProductRepository.findByType(normalized)
        );
    }

    @Override
    public List<PharmacyListResponse> advancedSearch(String name, String type, BigDecimal minPrice, BigDecimal maxPrice) {
        name = normalize(name);
        type = normalize(type);
        minPrice = safeMin(minPrice);
        maxPrice = safeMax(maxPrice);
        validatePriceRange(minPrice, maxPrice);
        List<PharmacyProductEntity> products = pharmacyProductRepository.advancedSearch(name, type, minPrice, maxPrice);
        if (products.isEmpty()) return Collections.emptyList();
        return toConverterPharmacy.toConverterPharmacyProductList(products);
    }

    public Optional<PharmacyListResponse> getPharmacyDetails(Long id) {
        if (id == null || !pharmacyProductRepository.existsById(id)) return Optional.empty();
        return pharmacyProductRepository.findByIdWithDetails(id)
                .map(product -> toConverterPharmacy.toConverterPharmacyProductList(List.of(product)).get(0));
    }

    public boolean checkAvailability(Long productId, Integer requiredQuantity) {
        if (productId == null || requiredQuantity == null || requiredQuantity <= 0) return false;
        if (!pharmacyProductRepository.existsById(productId)) return false;
        Integer availableQuantity = pharmacyProductRepository.checkInventoryQuantity(productId);
        return availableQuantity >= requiredQuantity;
    }

    // --- Private helper methods ---
    private String normalize(String input) {
        return (input != null && !input.trim().isEmpty()) ? input.trim() : null;
    }
    private BigDecimal safeMin(BigDecimal min) {
        return min != null ? min : BigDecimal.ZERO;
    }
    private BigDecimal safeMax(BigDecimal max) {
        return max != null ? max : BigDecimal.valueOf(Long.MAX_VALUE);
    }
    private void validatePriceRange(BigDecimal min, BigDecimal max) {
        if (min.compareTo(max) > 0) throw new IllegalArgumentException("minPrice must <= maxPrice");
    }
}
