package org.project.service.impl;

import lombok.RequiredArgsConstructor;
import org.project.converter.ConverterPharmacyProduct;
import org.project.entity.ProductEntity;
import org.project.enums.ProductLabel;
import org.project.enums.ProductSortType;
import org.project.model.response.PharmacyListResponse;
import org.project.repository.PharmacyRepository;
import org.project.service.PharmacyService;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PharmacyServiceImpl implements PharmacyService {
    private final PharmacyRepository pharmacyRepository;
    private final ConverterPharmacyProduct toConverterPharmacy;

    @Override
    public List<PharmacyListResponse> getAllPharmacies() {
        List<ProductEntity> products = pharmacyRepository.findAllWithCategory();
        return toConverterPharmacy.toConverterPharmacyProductList(products);
    }

    @Override
    public List<PharmacyListResponse> searchByName(String name) {
        String normalized = normalize(name);
        if (normalized == null) return getAllPharmacies();
        List<ProductEntity> products = pharmacyRepository.findByNameContainingIgnoreCase(normalized);
        return toConverterPharmacy.toConverterPharmacyProductList(products);
    }

    public List<PharmacyListResponse> sortProducts(List<PharmacyListResponse> products, ProductSortType sortType) {
        if (sortType == null) sortType = ProductSortType.DEFAULT;
        List<PharmacyListResponse> sortedProducts = new java.util.ArrayList<>(products);
        switch (sortType) {
            case DEFAULT:
                break;
            case PRICE_ASC:
                sortedProducts.sort(Comparator.comparing(PharmacyListResponse::getPrice, Comparator.nullsLast(Comparator.naturalOrder())));
                break;
            case PRICE_DESC:
                sortedProducts.sort(Comparator.comparing(PharmacyListResponse::getPrice, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
                break;
            case RATING:
                sortedProducts.sort(Comparator.comparing(PharmacyListResponse::getRating, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
                break;
            default:
                break;
        }
        return sortedProducts;
    }

    @Override
    public List<PharmacyListResponse> searchByCategory(Long categoryId) {
        if (categoryId == null) return getAllPharmacies();
        List<ProductEntity> products = pharmacyRepository.findAllByCategory(categoryId);
        return toConverterPharmacy.toConverterPharmacyProductList(products);
    }


    @Override
    public List<PharmacyListResponse> searchByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        minPrice = safeMin(minPrice);
        maxPrice = safeMax(maxPrice);
        validatePriceRange(minPrice, maxPrice);
        List<ProductEntity> products = pharmacyRepository.findByPriceBetween(minPrice, maxPrice);
        return toConverterPharmacy.toConverterPharmacyProductList(products);
    }

    @Override
    public List<PharmacyListResponse> searchByType(String type) {
        String normalized = normalize(type);
        if (normalized == null) return getAllPharmacies();
        try {
            ProductLabel label = ProductLabel.valueOf(normalized.toUpperCase());
            List<ProductEntity> products = pharmacyRepository.findByLabel(label);
            return toConverterPharmacy.toConverterPharmacyProductList(products);
        } catch (IllegalArgumentException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<PharmacyListResponse> searchByTag(String tagName) {
        String normalized = normalize(tagName);
        if (normalized == null) return getAllPharmacies();
        List<ProductEntity> products = pharmacyRepository.findByTag(normalized);
        return toConverterPharmacy.toConverterPharmacyProductList(products);
    }

    @Override
    public List<PharmacyListResponse> advancedSearch(String name, String type, String tagName, BigDecimal minPrice, BigDecimal maxPrice) {
        name = normalize(name);
        ProductLabel label = null;
        if (type != null && !type.trim().isEmpty()) {
            try {
                label = ProductLabel.valueOf(type.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                return Collections.emptyList();
            }
        }
        tagName = normalize(tagName);
        minPrice = safeMin(minPrice);
        maxPrice = safeMax(maxPrice);
        validatePriceRange(minPrice, maxPrice);
        List<ProductEntity> products = pharmacyRepository.advancedSearch(name, label, tagName, minPrice, maxPrice);
        return toConverterPharmacy.toConverterPharmacyProductList(products);
    }

    public Optional<PharmacyListResponse> getPharmacyDetails(Long id) {
        if (id == null) return Optional.empty();
        return pharmacyRepository.findByIdWithDetails(id)
                .map(product -> toConverterPharmacy.toConverterPharmacyProductList(List.of(product)).get(0));
    }

    public boolean checkAvailability(Long productId, Integer requiredQuantity) {
        if (productId == null || requiredQuantity == null || requiredQuantity <= 0) return false;
        Integer availableQuantity = pharmacyRepository.checkInventoryQuantity(productId);
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
