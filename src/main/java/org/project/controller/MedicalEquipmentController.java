package org.project.controller;

import lombok.RequiredArgsConstructor;
import org.project.entity.MedicalProductEntity;
import org.project.entity.ProductEntity;
import org.project.entity.StockRequestEntity;
import org.project.enums.ProductType;
import org.project.enums.StockStatus;
import org.project.enums.StockTransactionType;
import org.project.service.MedicalProductService;
import org.project.service.ProductService;
import org.project.service.StockRequestService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/medical-equipment")
@RequiredArgsConstructor
public class MedicalEquipmentController {

    private final ProductService productService;
    private final MedicalProductService medicalProductService;
    private final StockRequestService stockRequestService;

    @GetMapping
    public String getMedicalEquipmentPage(Model model) {
        List<ProductEntity> medicalProducts = productService.findAllByProductType(ProductType.MEDICAL_PRODUCT);
        List<StockRequestEntity> stockInRequests = stockRequestService.findAllByTransactionType(StockTransactionType.STOCK_IN);
        
        model.addAttribute("medicalProducts", medicalProducts);
        model.addAttribute("stockInRequests", stockInRequests);
        return "medical-equipment";
    }

    @GetMapping("/{id}")
    public String getMedicalEquipmentDetails(@PathVariable Long id, Model model) {
        ProductEntity product = productService.findById(id);
        model.addAttribute("product", product);
        return "medical-equipment-details";
    }

    @PostMapping("/create")
    public String createMedicalEquipment(@ModelAttribute ProductEntity product) {
        product.setProductType(ProductType.MEDICAL_PRODUCT);
        ProductEntity savedProduct = productService.save(product);
        
        MedicalProductEntity medicalProduct = new MedicalProductEntity();
        medicalProduct.setProductEntity(savedProduct);
        medicalProductService.save(medicalProduct);
        
        return "redirect:/medical-equipment";
    }

    @PostMapping("/update/{id}")
    public String updateMedicalEquipment(@PathVariable Long id, @ModelAttribute ProductEntity product) {
        ProductEntity existingProduct = productService.findById(id);
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setUnit(product.getUnit());
        existingProduct.setStockQuantities(product.getStockQuantities());
        existingProduct.setImageUrl(product.getImageUrl());
        
        productService.save(existingProduct);
        return "redirect:/medical-equipment";
    }

    @PostMapping("/delete/{id}")
    public String deleteMedicalEquipment(@PathVariable Long id) {
        productService.deleteById(id);
        return "redirect:/medical-equipment";
    }

    @PostMapping("/create-stock-request")
    public String createStockRequest(@ModelAttribute StockRequestEntity stockRequest) {
        stockRequest.setTransactionType(StockTransactionType.STOCK_IN);
        stockRequest.setRequestDate(Timestamp.valueOf(LocalDateTime.now()));
        stockRequest.setStatus(StockStatus.WAITING_FOR_DELIVERY);
        
        stockRequestService.save(stockRequest);
        return "redirect:/stock-in";
    }

    @PostMapping("/update-stock/{id}")
    public String updateStock(@PathVariable Long id, @RequestParam Integer quantity) {
        ProductEntity product = productService.findById(id);
        product.setStockQuantities(quantity);
        productService.save(product);
        return "redirect:/medical-equipment";
    }
} 