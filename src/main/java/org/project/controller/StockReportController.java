package org.project.controller;

import lombok.RequiredArgsConstructor;
import org.project.entity.ProductEntity;
import org.project.entity.StockRequestEntity;
import org.project.entity.StockRequestItemEntity;
import org.project.enums.StockTransactionType;
import org.project.model.response.ProductStockReportResponse;
import org.project.repository.ProductRepository;
import org.project.repository.StockRequestItemRepository;
import org.project.repository.StockRequestRepository;
import org.project.utils.StockReportUtils;
import org.project.utils.TimestampUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reports")
public class StockReportController {

    private final ProductRepository productRepository;
    private final StockRequestRepository stockRequestRepository;
    private final StockRequestItemRepository stockRequestItemRepository;
    
    // Reports main page
    @GetMapping
    public String reportsPage(Model model) {
        return "frontend/reports";
    }
    
    // Low stock report
    @GetMapping("/low-stock")
    @ResponseBody
    public List<ProductStockReportResponse> getLowStockReport(
            @RequestParam(defaultValue = "10") int threshold) {
        List<ProductEntity> products = productRepository.findAll();
        return StockReportUtils.generateLowStockReport(products, threshold);
    }
    
    // Expiring stock report
    @GetMapping("/expiring")
    @ResponseBody
    public List<ProductStockReportResponse> getExpiringStockReport(
            @RequestParam(defaultValue = "30") int daysThreshold) {
        List<StockRequestItemEntity> stockItems = stockRequestItemRepository.findAll();
        return StockReportUtils.generateExpiringStockReport(stockItems, daysThreshold);
    }
    
    // Stock movement report
    @GetMapping("/stock-movement")
    @ResponseBody
    public List<ProductStockReportResponse> getStockMovementReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) StockTransactionType type) {
        
        // Convert LocalDate to Timestamp
        Timestamp startTimestamp = Timestamp.valueOf(LocalDateTime.of(startDate, LocalTime.MIN));
        Timestamp endTimestamp = Timestamp.valueOf(LocalDateTime.of(endDate, LocalTime.MAX));
        
        List<StockRequestEntity> stockRequests;
        if (type != null) {
            stockRequests = stockRequestRepository.findByTransactionTypeAndDateRange(
                    type, startTimestamp, endTimestamp, null).getContent();
        } else {
            stockRequests = stockRequestRepository.findByDateRange(
                    startTimestamp, endTimestamp, null).getContent();
        }
        
        Map<Long, ProductStockReportResponse> reportMap = 
                StockReportUtils.generateStockMovementReport(stockRequests, startTimestamp, endTimestamp);
        
        return reportMap.values().stream().collect(Collectors.toList());
    }
    
    // Stock valuation report
    @GetMapping("/stock-valuation")
    @ResponseBody
    public List<ProductStockReportResponse> getStockValuationReport() {
        List<ProductEntity> products = productRepository.findAll();
        List<StockRequestItemEntity> stockItems = stockRequestItemRepository.findAll();
        return StockReportUtils.generateStockValuationReport(products, stockItems);
    }
} 