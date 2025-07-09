package org.project.controller;

import lombok.RequiredArgsConstructor;
import org.project.entity.MedicineEntity;
import org.project.entity.ProductEntity;
import org.project.entity.StockRequestEntity;
import org.project.enums.ProductType;
import org.project.enums.StockStatus;
import org.project.enums.StockTransactionType;
import org.project.service.MedicineService;
import org.project.service.ProductService;
import org.project.service.StockRequestService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/medicine")
@RequiredArgsConstructor
public class MedicineController {

    private final ProductService productService;
    private final MedicineService medicineService;
    private final StockRequestService stockRequestService;

    @GetMapping
    public String getMedicinePage(Model model) {
        List<ProductEntity> medicines = productService.findAllByProductType(ProductType.MEDICINE);
        List<StockRequestEntity> stockInRequests = stockRequestService.findAllByTransactionType(StockTransactionType.STOCK_IN);
        
        model.addAttribute("medicines", medicines);
        model.addAttribute("stockInRequests", stockInRequests);
        return "medicine";
    }

    @GetMapping("/{id}")
    public String getMedicineDetails(@PathVariable Long id, Model model) {
        ProductEntity product = productService.findById(id);
        model.addAttribute("product", product);
        return "medicine-details";
    }

    @PostMapping("/create")
    public String createMedicine(@ModelAttribute ProductEntity product) {
        product.setProductType(ProductType.MEDICINE);
        ProductEntity savedProduct = productService.save(product);
        
        MedicineEntity medicine = new MedicineEntity();
        medicine.setProductEntity(savedProduct);
        medicineService.save(medicine);
        
        return "redirect:/medicine";
    }

    @PostMapping("/update/{id}")
    public String updateMedicine(@PathVariable Long id, @ModelAttribute ProductEntity product) {
        ProductEntity existingProduct = productService.findById(id);
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setUnit(product.getUnit());
        existingProduct.setStockQuantities(product.getStockQuantities());
        existingProduct.setImageUrl(product.getImageUrl());
        
        productService.save(existingProduct);
        return "redirect:/medicine";
    }

    @PostMapping("/delete/{id}")
    public String deleteMedicine(@PathVariable Long id) {
        productService.deleteById(id);
        return "redirect:/medicine";
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
        return "redirect:/medicine";
    }
} 