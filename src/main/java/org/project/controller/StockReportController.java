package org.project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.entity.ProductEntity;
import org.project.entity.StockRequestItemEntity;
import org.project.entity.SupplierTransactionsEntity;
import org.project.enums.SupplierTransactionType;
import org.project.model.response.ProductStockReportResponse;
import org.project.repository.ProductRepository;
import org.project.repository.StockRequestItemRepository;
import org.project.repository.SupplierTransactionRepository;
import org.project.utils.StockReportUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for handling stock reports and analytics
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/reports")
public class StockReportController {

    private final ProductRepository productRepository;
    private final StockRequestItemRepository stockRequestItemRepository;
    private final SupplierTransactionRepository supplierTransactionRepository;
    
    /**
     * Reports main page displaying stock analytics
     * @param model Spring MVC Model
     * @return View name for reports page
     */
    @GetMapping
    public String reportsPage(Model model) {
        log.info("Loading reports page");
        
        try {
            // Get low stock products (below threshold of 10)
            List<ProductEntity> lowStockProducts = productRepository.findAll().stream()
                    .filter(product -> product.getStockQuantities() <= 10)
                    .collect(Collectors.toList());
            
            // Get transaction counts
            List<SupplierTransactionsEntity> stockInCount = supplierTransactionRepository.findByTransactionType(SupplierTransactionType.STOCK_IN);
            List<SupplierTransactionsEntity> stockOutCount = supplierTransactionRepository.findByTransactionType(SupplierTransactionType.STOCK_OUT);
            
            // Add data to model
            model.addAttribute("lowStockProducts", lowStockProducts);
            model.addAttribute("productCount", productRepository.count());
            model.addAttribute("stockInCount", stockInCount);
            model.addAttribute("stockOutCount", stockOutCount);
            
            // Dashboard data for the template
            Map<String, Object> dashboardData = new HashMap<>();
            dashboardData.put("productCount", productRepository.count());
            dashboardData.put("stockInCount", stockInCount);
            dashboardData.put("stockOutCount", stockOutCount);
            
            // Low stock products
            List<ProductStockReportResponse> lowStockReport = StockReportUtils.generateLowStockReport(
                    productRepository.findAll(), 10);
            dashboardData.put("lowStockProducts", lowStockReport);
            model.addAttribute("lowStockReport", lowStockReport);
            
            // Expiring products
            List<ProductStockReportResponse> expiringProducts = StockReportUtils.generateExpiringStockReport(
                    stockRequestItemRepository.findAll(), 30);
            dashboardData.put("expiringProducts", expiringProducts);
            model.addAttribute("expiringProducts", expiringProducts);
            
            // Stock valuation
            List<ProductStockReportResponse> valuationReport = StockReportUtils.generateStockValuationReport(
                    productRepository.findAll(), stockRequestItemRepository.findAll());
            model.addAttribute("valuationReport", valuationReport);
            
            // Recent transactions
            List<SupplierTransactionsEntity> recentTransactions = supplierTransactionRepository
                    .findTop10ByOrderByTransactionDateDesc();
            model.addAttribute("recentTransactions", recentTransactions);
            
            // Add current user for forms
            model.addAttribute("currentUser", getCurrentUser());
            
            log.debug("Reports page prepared with {} low stock products and {} expiring products", 
                    lowStockReport.size(), expiringProducts.size());
        } catch (Exception e) {
            log.error("Error preparing reports page data: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Lỗi khi tải dữ liệu báo cáo: " + e.getMessage());
        }
        
        return "templates_storage/reports";
    }
    
    /**
     * Low stock report page
     * @param threshold Stock threshold below which products are considered low stock
     * @param model Spring MVC Model
     * @return View name for low stock report
     */
    @GetMapping("/low-stock")
    public String getLowStockReport(
            @RequestParam(defaultValue = "10") int threshold,
            Model model) {
        log.info("Generating low stock report with threshold: {}", threshold);
        
        try {
            List<ProductEntity> products = productRepository.findAll();
            List<ProductStockReportResponse> report = StockReportUtils.generateLowStockReport(products, threshold);
            model.addAttribute("lowStockReport", report);
            model.addAttribute("threshold", threshold);
            log.debug("Low stock report generated with {} products", report.size());
        } catch (Exception e) {
            log.error("Error generating low stock report: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Lỗi khi tạo báo cáo hàng sắp hết: " + e.getMessage());
        }
        
        return "templates_storage/reports-low-stock";
    }
    
    /**
     * Expiring stock report page
     * @param daysThreshold Days threshold for expiration
     * @param model Spring MVC Model
     * @return View name for expiring stock report
     */
    @GetMapping("/expiring")
    public String getExpiringStockReport(
            @RequestParam(defaultValue = "30") int daysThreshold,
            Model model) {
        log.info("Generating expiring stock report with days threshold: {}", daysThreshold);
        
        try {
            List<StockRequestItemEntity> stockItems = stockRequestItemRepository.findAll();
            List<ProductStockReportResponse> report = StockReportUtils.generateExpiringStockReport(stockItems, daysThreshold);
            model.addAttribute("expiringReport", report);
            model.addAttribute("daysThreshold", daysThreshold);
            log.debug("Expiring stock report generated with {} products", report.size());
        } catch (Exception e) {
            log.error("Error generating expiring stock report: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Lỗi khi tạo báo cáo hàng sắp hết hạn: " + e.getMessage());
        }
        
        return "templates_storage/reports-expiring";
    }
    
    /**
     * Stock movement report page
     * @param startDate Start date for report period
     * @param endDate End date for report period
     * @param type Optional transaction type filter
     * @param model Spring MVC Model
     * @return View name for stock movement report
     */
    @GetMapping("/stock-movement")
    public String getStockMovementReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) SupplierTransactionType type,
            Model model) {
        
        log.info("Generating stock movement report from {} to {} with type: {}", startDate, endDate, type);
        
        try {
            // Convert LocalDate to LocalDateTime
            LocalDateTime startTimestamp = LocalDateTime.of(startDate, LocalTime.MIN);
            LocalDateTime endTimestamp = LocalDateTime.of(endDate, LocalTime.MAX);
            
            // Convert LocalDateTime to Timestamp for repository methods
            Timestamp startTs = Timestamp.valueOf(startTimestamp);
            Timestamp endTs = Timestamp.valueOf(endTimestamp);
            
            List<SupplierTransactionsEntity> transactions;
            if (type != null) { 
                transactions = supplierTransactionRepository.findByTransactionTypeAndTransactionDateBetween(
                        type, startTs, endTs);
            } else {
                transactions = (List<SupplierTransactionsEntity>) supplierTransactionRepository.findByTransactionDateBetween(startTs, endTs, PageRequest.of(0, 1000));
            }
            
            Map<Long, ProductStockReportResponse> reportMap = 
                    StockReportUtils.generateStockMovementReport(transactions, startTs, endTs);
            
            List<ProductStockReportResponse> result = new ArrayList<>(reportMap.values());
            
            model.addAttribute("movementReport", result);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
            model.addAttribute("transactionType", type);
            log.debug("Stock movement report generated with {} products", result.size());
        } catch (Exception e) {
            log.error("Error generating stock movement report: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Lỗi khi tạo báo cáo di chuyển hàng: " + e.getMessage());
        }
        
        return "templates_storage/reports-stock-movement";
    }
    
    /**
     * Stock valuation report page
     * @param model Spring MVC Model
     * @return View name for stock valuation report
     */
    @GetMapping("/stock-valuation")
    public String getStockValuationReport(Model model) {
        log.info("Generating stock valuation report");
        
        try {
            List<ProductEntity> products = productRepository.findAll();
            List<StockRequestItemEntity> stockItems = stockRequestItemRepository.findAll();
            List<ProductStockReportResponse> result = StockReportUtils.generateStockValuationReport(products, stockItems);
            
            model.addAttribute("valuationReport", result);
            log.debug("Stock valuation report generated with {} products", result.size());
        } catch (Exception e) {
            log.error("Error generating stock valuation report: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Lỗi khi tạo báo cáo giá trị hàng tồn kho: " + e.getMessage());
        }
        
        return "templates_storage/reports-valuation";
    }
    
    /**
     * Test method to display sample data in the reports page
     * @param model Spring MVC Model
     * @return View name for reports page with sample data
     */
    @GetMapping("/test")
    public String testReportsPage(Model model) {
        log.info("Loading test reports page with sample data");
        
        try {
            // Create sample data for reports
            List<ProductStockReportResponse> lowStockReport = createSampleLowStockReportData();
            List<ProductStockReportResponse> expiringProducts = createSampleExpiringStockReportData();
            List<ProductStockReportResponse> valuationReport = createSampleValuationReportData();
            
            // Add data to model
            model.addAttribute("lowStockProducts", lowStockReport);
            model.addAttribute("productCount", 25);
            model.addAttribute("stockInCount", 18);
            model.addAttribute("stockOutCount", 12);
            
            // Dashboard data
            Map<String, Object> dashboardData = new HashMap<>();
            dashboardData.put("productCount", 25);
            dashboardData.put("stockInCount", 18);
            dashboardData.put("stockOutCount", 12);
            dashboardData.put("lowStockProducts", lowStockReport);
            
            model.addAttribute("lowStockReport", lowStockReport);
            model.addAttribute("expiringProducts", expiringProducts);
            model.addAttribute("valuationReport", valuationReport);
            
            log.debug("Test reports page prepared with {} low stock products and {} expiring products", 
                    lowStockReport.size(), expiringProducts.size());
        } catch (Exception e) {
            log.error("Error preparing test reports page data: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Lỗi khi tải dữ liệu báo cáo mẫu: " + e.getMessage());
        }
        
        return "templates_storage/reports";
    }
    
    /**
     * Creates sample low stock report data
     * @return List of sample low stock report responses
     */
    private List<ProductStockReportResponse> createSampleLowStockReportData() {
        List<ProductStockReportResponse> sampleData = new ArrayList<>();
        String[] productNames = {
            "Paracetamol", "Amoxicillin", "Omeprazole", 
            "Ibuprofen", "Cetirizine"
        };
        
        for (int i = 0; i < 5; i++) {
            ProductStockReportResponse response = new ProductStockReportResponse();
            response.setProductId((long) (i + 1));
            response.setProductName(productNames[i]);
            response.setCurrentStock(i + 3); // Low stock values
            response.setThreshold(10);
            response.setUnitPrice(new BigDecimal("" + (50 + i * 10)));
            response.setTotalValue(response.getUnitPrice().multiply(new BigDecimal(response.getCurrentStock())));
            response.setStatus("Thấp");
            
            sampleData.add(response);
        }
        
        return sampleData;
    }
    
    /**
     * Creates sample expiring stock report data
     * @return List of sample expiring stock report responses
     */
    private List<ProductStockReportResponse> createSampleExpiringStockReportData() {
        List<ProductStockReportResponse> sampleData = new ArrayList<>();
        String[] productNames = {
            "Paracetamol", "Amoxicillin", "Omeprazole"
        };
        
        Random random = new Random();
        
        for (int i = 0; i < 3; i++) {
            ProductStockReportResponse response = new ProductStockReportResponse();
            response.setProductId((long) (i + 1));
            response.setProductName(productNames[i]);
            response.setCurrentStock(20 + random.nextInt(30));
            response.setUnitPrice(new BigDecimal("" + (50 + i * 10)));
            response.setTotalValue(response.getUnitPrice().multiply(new BigDecimal(response.getCurrentStock())));
            
            // Set expiration date to near future
            LocalDate expirationDate = LocalDate.now().plusDays(10 + i * 5);
            response.setExpirationDate(java.sql.Date.valueOf(expirationDate));
            
            // Calculate days until expiration
            long daysUntilExpiration = java.time.temporal.ChronoUnit.DAYS.between(
                    LocalDate.now(), expirationDate);
            
            // Set status based on days until expiration
            if (daysUntilExpiration < 15) {
                response.setStatus("Sắp hết hạn");
                response.setRecommendedAction("Ưu tiên sử dụng");
            } else {
                response.setStatus("Bình thường");
                response.setRecommendedAction("Kiểm tra định kỳ");
            }
            
            sampleData.add(response);
        }
        
        return sampleData;
    }
    
    /**
     * Creates sample valuation report data
     * @return List of sample valuation report responses
     */
    private List<ProductStockReportResponse> createSampleValuationReportData() {
        List<ProductStockReportResponse> sampleData = new ArrayList<>();
        String[] productNames = {
            "Paracetamol", "Amoxicillin", "Omeprazole", 
            "Ibuprofen", "Cetirizine", "Vitamin C", "Aspirin"
        };
        
        Random random = new Random();
        
        for (int i = 0; i < productNames.length; i++) {
            ProductStockReportResponse response = new ProductStockReportResponse();
            response.setProductId((long) (i + 1));
            response.setProductName(productNames[i]);
            response.setCurrentStock(50 + random.nextInt(150));
            response.setUnitPrice(new BigDecimal("" + (30 + random.nextInt(70))));
            response.setTotalValue(response.getUnitPrice().multiply(new BigDecimal(response.getCurrentStock())));
            
            sampleData.add(response);
        }
        
        return sampleData;
    }
    
    /**
     * Get current user - placeholder method
     * @return Current user object or null
     */
    private Object getCurrentUser() {
        // TODO: Implement proper user authentication
        // For now, return a simple object with required properties
        return new Object() {
            public Long getId() { return 256L; }
            public String getFullName() { return "Người dùng"; }
            public String getRoleName() { return "STAFF"; }
            public String getAvatar() { return "/templates_storage/assets/images/avatar.png"; }
        };
    }
} 