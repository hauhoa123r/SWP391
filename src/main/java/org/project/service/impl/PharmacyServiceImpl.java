package org.project.service.impl;

import lombok.RequiredArgsConstructor;
import org.project.converter.ConverterPharmacyProduct;
import org.project.entity.ProductEntity;
import org.project.enums.ProductLabel;
import org.project.enums.ProductSortType;
import org.project.model.response.PharmacyListResponse;
import org.project.repository.PharmacyRepository;
import org.project.service.PharmacyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PharmacyServiceImpl implements PharmacyService {

    private final PharmacyRepository pharmacyRepository;
    private final ConverterPharmacyProduct converter;

    public List<PharmacyListResponse> findTop10Products() {
        List<ProductEntity> entities = pharmacyRepository.findAll(
                PageRequest.of(0, 10, Sort.by("rating").descending())).getContent();
        return entities.stream().map(this::convertToDto).collect(Collectors.toList());
    }

@Override
public PharmacyListResponse findById(Long id) {
    return pharmacyRepository.findById(id)
            .map(this::convertToDto)
            .orElse(null);
}

    private PharmacyListResponse convertToDto(ProductEntity entity) {
        PharmacyListResponse dto = new PharmacyListResponse();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setRating(calculateRating(entity));
        dto.setCategory(entity.getCategoryEntities() != null && !entity.getCategoryEntities().isEmpty()
                ? entity.getCategoryEntities().iterator().next().getName() : null);
        // ... ánh xạ các trường khác
        return dto;
    }

    private double calculateRating(ProductEntity entity) {
        return entity.getReviewEntities() != null && !entity.getReviewEntities().isEmpty()
                ? entity.getReviewEntities().stream().mapToDouble(review -> review.getRating()).average().orElse(0.0)
                : 0.0;
    }

}

//    @Override
//    public Page<PharmacyListResponse> getAllProducts(Pageable pageable, ProductSortType sortType) {
//        Pageable sortedPageable = createSortedPageable(pageable, sortType);
//        Page<ProductEntity> productPage = pharmacyRepository.findAll(sortedPageable);
//        return productPage.map(this::convertToDto);
//    }
//
//    @Override
//    public Page<PharmacyListResponse> searchByName(String name, Pageable pageable, ProductSortType sortType) {
//        Pageable sortedPageable = createSortedPageable(pageable, sortType);
//        Page<ProductEntity> productPage = pharmacyRepository.findByNameContainingIgnoreCase(name, sortedPageable);
//        return productPage.map(this::convertToDto);
//    }
//
//    @Override
//    public List<PharmacyListResponse> sortProducts(List<PharmacyListResponse> products, ProductSortType sortType) {
//        var sort = (sortType == null) ? ProductSortType.DEFAULT : sortType;
//        var sortedProducts = new ArrayList<>(products);
//        switch (sort) {
//            case DEFAULT -> {}
//            case PRICE_ASC -> sortedProducts.sort(Comparator.comparing(PharmacyListResponse::getPrice, Comparator.nullsLast(Comparator.naturalOrder())));
//            case PRICE_DESC -> sortedProducts.sort(Comparator.comparing(PharmacyListResponse::getPrice, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
//            case RATING -> sortedProducts.sort(Comparator.comparing(PharmacyListResponse::getRating, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
//        }
//        return sortedProducts;
//    }
//
//    private Pageable createSortedPageable(Pageable pageable, ProductSortType sortType) {
//        if (sortType == null || sortType == ProductSortType.DEFAULT) {
//            return pageable;
//        }
//        Sort sort = switch (sortType) {
//            case PRICE_ASC -> Sort.by("price").ascending();
//            case PRICE_DESC -> Sort.by("price").descending();
//            case RATING -> Sort.by("rating").descending(); // Giả sử có trường rating trong ProductEntity
//            default -> Sort.unsorted();
//        };
//        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
//    }
//
//    @Override
//    public Page<PharmacyListResponse> searchByCategory(Long categoryId, Pageable pageable, ProductSortType sortType) {
//        if (categoryId == null) return getAllProducts(pageable, sortType);
//        var products = pharmacyRepository.findByCategoryEntities_Id(categoryId, pageable);
//        return products.map(this::convertToDto);
//    }
//
//    @Override
//    public Page<PharmacyListResponse> searchByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
//        var min = safeMin(minPrice);
//        var max = safeMax(maxPrice);
//        validatePriceRange(min, max);
//        var products = pharmacyRepository.findByPriceBetween(min, max, pageable);
//        return products.map(this::convertToDto);
//    }
//
//    @Override
//    public Page<PharmacyListResponse> searchByType(String type, Pageable pageable) {
//        var normalized = normalize(type);
//        if (normalized == null) return getAllProducts(pageable);
//        try {
//            var label = ProductLabel.valueOf(normalized.toUpperCase());
//            var products = pharmacyRepository.findByLabel(label, pageable);
//            return products.map(this::convertToDto);
//        } catch (IllegalArgumentException e) {
//            return Page.empty(pageable);
//        }
//    }
//
//    private PharmacyListResponse convertToDto(ProductEntity entity) {
//        var dto = new PharmacyListResponse();
//        dto.setId(entity.getId());
//        dto.setName(entity.getName());
//        dto.setPrice(entity.getPrice());
//        dto.setImageUrl(entity.getImageUrl());
//        dto.setDescription(entity.getDescription());
//        dto.setLabel(entity.getLabel() != null ? entity.getLabel().name() : null);
//        double avgRating = 0.0;
//        if (entity.getReviewEntities() != null && !entity.getReviewEntities().isEmpty()) {
//            avgRating = entity.getReviewEntities()
//                    .stream()
//                    .filter(r -> r.getRating() != null)
//                    .mapToDouble(r -> r.getRating().doubleValue())
//                    .average()
//                    .orElse(0.0);
//        }
//        dto.setRating(avgRating);
//
//        return dto;
//    }
//
//    private String normalize(String input) {
//        return (input != null && !input.trim().isEmpty()) ? input.trim() : null;
//    }
//
//    private BigDecimal safeMin(BigDecimal min) {
//        return min != null ? min : BigDecimal.ZERO;
//    }
//
//    private BigDecimal safeMax(BigDecimal max) {
//        return max != null ? max : BigDecimal.valueOf(Long.MAX_VALUE);
//    }
//
//    private void validatePriceRange(BigDecimal min, BigDecimal max) {
//        if (min.compareTo(max) > 0) throw new IllegalArgumentException("minPrice must be less than or equal to maxPrice");
//    }
