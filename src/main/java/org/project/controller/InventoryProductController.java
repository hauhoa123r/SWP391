package org.project.controller;

import lombok.RequiredArgsConstructor;
import org.project.entity.ProductEntity;
import org.project.enums.ProductType;
import org.project.model.response.ProductResponse;
import org.project.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/warehouse/inventory/products")
public class InventoryProductController {

    private final ProductRepository productRepository;
    
    // Medical Equipment page
    @GetMapping("/medical-equipment")
    public String medicalEquipmentPage(Model model) {
        return "frontend/medical-equipment";
    }
    
    // Medicine page
    @GetMapping("/medicine")
    public String medicinePage(Model model) {
        return "frontend/medicine";
    }
    
    // Test Supplies page
    @GetMapping("/test-supplies")
    public String testSuppliesPage(Model model) {
        return "frontend/test-supplies";
    }
    
    // API endpoints for AJAX calls
    
    // Get all medical equipment products
    @GetMapping("/api/medical-equipment")
    @ResponseBody
    public Page<ProductResponse> getMedicalEquipmentProducts(
            @RequestParam(required = false) String name,
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        
        Page<ProductEntity> products;
        if (name != null && !name.isEmpty()) {
            products = productRepository.findByProductTypeAndNameContainingIgnoreCase(
                    ProductType.MEDICAL_PRODUCT, name, pageable);
        } else {
            products = productRepository.findByProductType(ProductType.MEDICAL_PRODUCT, pageable);
        }
        
        return products.map(this::convertToProductResponse);
    }
    
    // Get all medicine products
    @GetMapping("/api/medicine")
    @ResponseBody
    public Page<ProductResponse> getMedicineProducts(
            @RequestParam(required = false) String name,
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        
        Page<ProductEntity> products;
        if (name != null && !name.isEmpty()) {
            products = productRepository.findByProductTypeAndNameContainingIgnoreCase(
                    ProductType.MEDICINE, name, pageable);
        } else {
            products = productRepository.findByProductType(ProductType.MEDICINE, pageable);
        }
        
        return products.map(this::convertToProductResponse);
    }
    
    // Get all test supplies products
    @GetMapping("/api/test-supplies")
    @ResponseBody
    public Page<ProductResponse> getTestSuppliesProducts(
            @RequestParam(required = false) String name,
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        
        Page<ProductEntity> products;
        if (name != null && !name.isEmpty()) {
            products = productRepository.findByProductTypeAndNameContainingIgnoreCase(
                    ProductType.TEST, name, pageable);
        } else {
            products = productRepository.findByProductType(ProductType.TEST, pageable);
        }
        
        return products.map(this::convertToProductResponse);
    }
    
    // Get low stock products
    @GetMapping("/api/low-stock")
    @ResponseBody
    public List<ProductResponse> getLowStockProducts(@RequestParam(defaultValue = "10") int threshold) {
        List<ProductEntity> products = productRepository.findByStockQuantitiesLessThanEqual(threshold);
        return products.stream()
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());
    }
    
    // Helper method to convert entity to response
    private ProductResponse convertToProductResponse(ProductEntity entity) {
        ProductResponse response = new ProductResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setDescription(entity.getDescription());
        response.setPrice(entity.getPrice());
        response.setUnit(entity.getUnit());
        response.setStockQuantity(entity.getStockQuantities());
        response.setImageUrl(entity.getImageUrl());
        response.setProductType(entity.getProductType());
        response.setProductStatus(entity.getProductStatus());
        response.setLabel(entity.getLabel());
        response.setAverageRating(entity.getAverageRating());
        response.setReviewCount(entity.getReviewCount());
        return response;
    }
} 