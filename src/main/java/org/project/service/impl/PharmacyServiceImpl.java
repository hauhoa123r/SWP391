package org.project.service.impl;

import lombok.RequiredArgsConstructor;
import org.project.converter.ConverterPharmacyProduct;
import org.project.entity.ProductEntity;
import org.project.model.response.PharmacyListResponse;
import org.project.repository.PharmacyRepository;
import org.project.service.PharmacyService;
import org.springframework.data.domain.PageRequest;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PharmacyServiceImpl implements PharmacyService {

    private final PharmacyRepository pharmacyRepository;
    private final ConverterPharmacyProduct converter;

    public List<PharmacyListResponse> findTop10Products() {
        // Fetch first page without sorting by a non-existent column, then sort in memory by calculated rating
        List<ProductEntity> entities = new ArrayList<>(pharmacyRepository.findAll(PageRequest.of(0, 100)).getContent());
        // Sort in-memory
        entities.sort((e1, e2) -> Double.compare(calculateRating(e2), calculateRating(e1)));
        if (entities.size() > 10) {
            entities = entities.subList(0, 10);
        }
        return entities.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    public PharmacyListResponse findById(Long id) {
        return pharmacyRepository.findById(id)
                .map(this::convertToDto)
                .orElse(null);
    }

    @Override
    public List<PharmacyListResponse> findRelatedProducts(Long productId, int limit) {
        // Method to fetch related products based on category
        ProductEntity product = pharmacyRepository.findById(productId).orElse(null);
        if (product == null || product.getCategoryEntities() == null || product.getCategoryEntities().isEmpty()) {
            return new ArrayList<>();
        }
        Long categoryId = product.getCategoryEntities().iterator().next().getId();
        List<ProductEntity> relatedEntities = new ArrayList<>(pharmacyRepository.findByCategoryEntities_Id(categoryId, PageRequest.of(0, limit + 1)).getContent());
        // Sort in-memory by calculated rating
        relatedEntities.sort((e1, e2) -> Double.compare(calculateRating(e2), calculateRating(e1)));
        return relatedEntities.stream()
                .filter(entity -> !entity.getId().equals(productId))
                .limit(limit)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private PharmacyListResponse convertToDto(ProductEntity entity) {
        PharmacyListResponse dto = new PharmacyListResponse();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setImageUrl(entity.getImageUrl());
        dto.setPrice(entity.getPrice());
        dto.setRating(calculateRating(entity));
        // map additional fields
        dto.setLabel(entity.getLabel() != null ? entity.getLabel().name() : null);
        dto.setStatus(entity.getProductStatus() != null ? entity.getProductStatus().name() : null);
        dto.setStockQuantity(entity.getStockQuantities());
        dto.setCategory(entity.getCategoryEntities() != null && !entity.getCategoryEntities().isEmpty()
                ? entity.getCategoryEntities().iterator().next().getName() : null);
        dto.setTags(entity.getProductTagEntities() != null
                ? entity.getProductTagEntities().stream().map(tag -> tag.getId().getName()).toList()
                : null);
        return dto;
    }

    @Override
    public Long findFirstProductId() {
        return pharmacyRepository.findFirstProductId();
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
        // dto.setImageUrl(entity.getImageUrl());
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