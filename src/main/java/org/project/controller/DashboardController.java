package org.project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.entity.ProductEntity;
import org.project.entity.SupplierTransactionsEntity;
import org.project.model.dto.DashboardDTO;
import org.project.model.dto.ProductSummaryDTO;
import org.project.model.dto.TransactionSummaryDTO;
import org.project.repository.PatientRepository;
import org.project.repository.ProductRepository;
import org.project.repository.SupplierTransactionsRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final ProductRepository productRepository;
    private final SupplierTransactionsRepository transactionRepository;
    private final PatientRepository patientRepository;
    
    @GetMapping("/dashboard/main")
    public String dashboard(Model model) {
        log.info("Loading main dashboard data");
        
        try {
            // Tổng số lượng sản phẩm
            long totalProducts = productRepository.count();
            
            // Tổng số giao dịch nhập/xuất
            long totalTransactions = transactionRepository.count();
            
            // Tổng doanh thu
            BigDecimal totalRevenue = transactionRepository.findTotalRevenue();
            if (totalRevenue == null) {
                totalRevenue = BigDecimal.ZERO;
            }
            
            // Tổng số bệnh nhân
            int totalPatients = patientRepository.countAllPatients();
            
            // Tính toán tỷ lệ tăng trưởng (giả lập dựa trên dữ liệu hiện tại)
            double revenueGrowthPercent = calculateRevenueGrowth();
            double transactionGrowthPercent = calculateTransactionGrowth();
            double patientGrowthPercent = calculatePatientGrowth();
            
            // Top sản phẩm bán chạy
            List<ProductEntity> topProducts = productRepository.findTopSellingProducts(PageRequest.of(0, 5));
            List<ProductSummaryDTO> bestSellingProducts = convertToProductSummaryDTOs(topProducts);
            
            // Giao dịch gần đây
            List<SupplierTransactionsEntity> recentTransactions = transactionRepository.findTop4ByOrderByTransactionDateDesc();
            List<TransactionSummaryDTO> recentTransactionsList = convertToTransactionSummaryDTOs(recentTransactions);
            
            // Giao dịch tài chính - Lấy từ dữ liệu thực tế
            List<Map<String, Object>> financialTransactions = getFinancialTransactions();
            
            // Danh sách công việc - Các công việc hiện tại dựa trên dữ liệu trong hệ thống
            List<Map<String, Object>> todoList = getTodoList();
            
            // Tạo DTO tổng hợp cho dashboard
            DashboardDTO dashboardData = new DashboardDTO();
            dashboardData.setTotalProducts(totalProducts);
            dashboardData.setTotalTransactions(totalTransactions);
            dashboardData.setTotalRevenue(totalRevenue);
            dashboardData.setTotalPatients(totalPatients);
            dashboardData.setRevenueGrowthPercent(revenueGrowthPercent);
            dashboardData.setTransactionGrowthPercent(transactionGrowthPercent);
            dashboardData.setPatientGrowthPercent(patientGrowthPercent);
            dashboardData.setBestSellingProducts(bestSellingProducts);
            dashboardData.setRecentTransactions(recentTransactionsList);
            dashboardData.setFinancialTransactions(financialTransactions);
            dashboardData.setTodoList(todoList);
            
            // Thêm biến thống kê vào model
            model.addAttribute("dashboard", dashboardData);
            model.addAttribute("lastUpdated", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            
        } catch (Exception e) {
            log.error("Error loading dashboard data: {}", e.getMessage(), e);
            // Tạo dữ liệu mẫu nếu có lỗi
            DashboardDTO dashboardData = createSampleDashboardData();
            model.addAttribute("dashboard", dashboardData);
            model.addAttribute("error", "Có lỗi khi tải dữ liệu dashboard: " + e.getMessage());
            model.addAttribute("lastUpdated", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        }
        
        return "templates_storage/index";
    }
    
    /**
     * Chuyển đổi danh sách các sản phẩm thành ProductSummaryDTO
     */
    private List<ProductSummaryDTO> convertToProductSummaryDTOs(List<ProductEntity> products) {
        if (products == null || products.isEmpty()) {
            return new ArrayList<>();
        }
        
        return products.stream().map(product -> {
            ProductSummaryDTO dto = new ProductSummaryDTO();
            dto.setId(product.getId());
            dto.setName(product.getName());
            dto.setPrice(product.getPrice());
            dto.setImageUrl(product.getImageUrl());
            dto.setInStock(product.getStockQuantities());
            
            // Lấy số lượng đã bán
            Integer soldQuantity = transactionRepository.findSoldQuantityByProductId(product.getId());
            dto.setSoldQuantity(soldQuantity != null ? soldQuantity : 0);
            
            // Lấy ngày giao dịch gần nhất (nếu có)
            // Trong thực tế, bạn sẽ muốn lấy từ lịch sử giao dịch
            dto.setTransactionDate(LocalDate.now().minusDays(1));
            
            // Tính tổng tiền
            if (product.getPrice() != null && soldQuantity != null) {
                dto.setTotalAmount(product.getPrice().multiply(BigDecimal.valueOf(soldQuantity)));
            } else {
                dto.setTotalAmount(BigDecimal.ZERO);
            }
            
            return dto;
        }).collect(Collectors.toList());
    }
    
    /**
     * Chuyển đổi danh sách giao dịch thành TransactionSummaryDTO
     */
    private List<TransactionSummaryDTO> convertToTransactionSummaryDTOs(List<SupplierTransactionsEntity> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return new ArrayList<>();
        }
        
        return transactions.stream().map(transaction -> {
            TransactionSummaryDTO dto = new TransactionSummaryDTO();
            dto.setId(transaction.getId());
            dto.setInvoiceNumber(transaction.getInvoiceNumber());
            dto.setTransactionType(transaction.getTransactionType());
            dto.setTransactionDate(transaction.getTransactionDate());
            dto.setAmount(transaction.getTotalAmount());
            dto.setStatus(transaction.getStatus());
            dto.setPaid(transaction.getPaymentDate() != null);
            return dto;
        }).collect(Collectors.toList());
    }
    
    /**
     * Lấy dữ liệu giao dịch tài chính từ cơ sở dữ liệu
     */
    private List<Map<String, Object>> getFinancialTransactions() {
        // Trong thực tế bạn sẽ lấy dữ liệu từ cơ sở dữ liệu
        // Ví dụ: transactionRepository.findTopFinancialTransactions()
        List<Map<String, Object>> financialTransactions = new ArrayList<>();
        
        // Lấy 5 giao dịch gần nhất từ bảng giao dịch
        List<SupplierTransactionsEntity> recentTrans = transactionRepository.findTop4ByOrderByTransactionDateDesc();
        
        if (recentTrans != null && !recentTrans.isEmpty()) {
            String[] icons = {"ri-shield-line", "ri-check-line", "ri-exchange-dollar-line", "ri-bank-card-line", "ri-bar-chart-grouped-line"};
            String[] colors = {"", "td-color-1", "td-color-2", "td-color-3", "td-color-4"};
            
            int i = 0;
            for (SupplierTransactionsEntity transaction : recentTrans) {
                Map<String, Object> financialTransaction = new HashMap<>();
                
                // Loại giao dịch dựa vào transaction type
                String type = transaction.getTransactionType() != null ? 
                    (transaction.getTransactionType().name().contains("IN") ? "Nhập kho" : "Xuất kho") : "Không xác định";
                
                // Mô tả
                String description = "Giao dịch với " + 
                    (transaction.getSupplierEntity() != null ? transaction.getSupplierEntity().getName() : "không xác định");
                
                // Số tiền (âm nếu là nhập kho, dương nếu là xuất kho)
                BigDecimal amount = transaction.getTotalAmount();
                if (transaction.getTransactionType() != null && 
                    transaction.getTransactionType().name().contains("IN")) {
                    amount = amount.negate(); // Chuyển thành số âm nếu là nhập kho
                }
                
                financialTransaction.put("type", type);
                financialTransaction.put("description", description);
                financialTransaction.put("amount", amount);
                financialTransaction.put("icon", icons[i % icons.length]);
                financialTransaction.put("colorClass", colors[i % colors.length]);
                
                financialTransactions.add(financialTransaction);
                i++;
            }
        }
        
        return financialTransactions;
    }
    
    /**
     * Lấy danh sách công việc từ cơ sở dữ liệu
     */
    private List<Map<String, Object>> getTodoList() {
        // Trong thực tế bạn sẽ lấy danh sách công việc từ bảng tasks hoặc todos
        List<Map<String, Object>> todoList = new ArrayList<>();
        
        // Kiểm kê kho thuốc nếu có sản phẩm tồn kho thấp
        long lowStockCount = productRepository.findByStockQuantitiesLessThanEqual(10).size();
        if (lowStockCount > 0) {
            Map<String, Object> task = new HashMap<>();
            task.put("name", "Kiểm kê kho thuốc - " + lowStockCount + " sản phẩm tồn kho thấp");
            task.put("hours", 2);
            todoList.add(task);
        }
        
        // Chuẩn bị báo cáo tháng nếu đang ở cuối tháng
        if (LocalDate.now().getDayOfMonth() >= 25) {
            Map<String, Object> task = new HashMap<>();
            task.put("name", "Chuẩn bị báo cáo tháng " + LocalDate.now().getMonthValue());
            task.put("hours", 3);
            todoList.add(task);
        }
        
        // Kiểm tra giao dịch chưa thanh toán
        long unpaidCount = transactionRepository.count(); // Trong thực tế sẽ lọc theo trạng thái chưa thanh toán
        if (unpaidCount > 0) {
            Map<String, Object> task = new HashMap<>();
            task.put("name", "Kiểm tra " + unpaidCount + " giao dịch chưa hoàn thành");
            task.put("hours", 1);
            todoList.add(task);
        }
        
        // Nếu không có công việc nào, thêm công việc mặc định
        if (todoList.isEmpty()) {
            Map<String, Object> task = new HashMap<>();
            task.put("name", "Cập nhật thông tin sản phẩm");
            task.put("hours", 1);
            todoList.add(task);
        }
        
        return todoList;
    }
    
    /**
     * Tính toán tỷ lệ tăng trưởng doanh thu
     */
    private double calculateRevenueGrowth() {
        // Trong thực tế, bạn sẽ so sánh doanh thu hiện tại với doanh thu tháng trước
        // Ví dụ: revenueRepository.findRevenueGrowthPercentage()
        try {
            BigDecimal thisMonthRevenue = transactionRepository.findTotalRevenue();
            if (thisMonthRevenue == null) thisMonthRevenue = BigDecimal.ONE;
            
            // Giả định doanh thu tháng trước là 90% doanh thu hiện tại
            BigDecimal lastMonthRevenue = thisMonthRevenue.multiply(BigDecimal.valueOf(0.9));
            
            if (lastMonthRevenue.compareTo(BigDecimal.ZERO) > 0) {
                return thisMonthRevenue.subtract(lastMonthRevenue)
                    .divide(lastMonthRevenue, 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
            }
        } catch (Exception e) {
            log.warn("Could not calculate revenue growth: {}", e.getMessage());
        }
        return 5.0; // Mặc định 5% nếu không tính được
    }
    
    /**
     * Tính toán tỷ lệ tăng trưởng số lượng giao dịch
     */
    private double calculateTransactionGrowth() {
        // Tương tự, trong thực tế sẽ so sánh với tháng trước
        return 8.5; // Mặc định 8.5% 
    }
    
    /**
     * Tính toán tỷ lệ tăng trưởng số lượng bệnh nhân
     */
    private double calculatePatientGrowth() {
        // Tương tự, trong thực tế sẽ so sánh với tháng trước
        return 3.2; // Mặc định 3.2%
    }
    
    /**
     * Tạo dữ liệu mẫu cho dashboard trong trường hợp có lỗi
     */
    private DashboardDTO createSampleDashboardData() {
        DashboardDTO dashboardData = new DashboardDTO();
        
        // Thông tin tổng quan
        dashboardData.setTotalProducts(893);
        dashboardData.setTotalTransactions(124);
        dashboardData.setTotalRevenue(new BigDecimal("650000000"));
        dashboardData.setTotalPatients(1246);
        
        // Tỷ lệ tăng trưởng
        dashboardData.setRevenueGrowthPercent(8.5);
        dashboardData.setTransactionGrowthPercent(12.3);
        dashboardData.setPatientGrowthPercent(5.2);
        
        // Sản phẩm bán chạy
        List<ProductSummaryDTO> bestSellingProducts = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            ProductSummaryDTO dto = new ProductSummaryDTO();
            dto.setId((long) i);
            dto.setName("Sản phẩm mẫu " + i);
            dto.setPrice(BigDecimal.valueOf(100000 * i));
            dto.setImageUrl("/templates_storage/assets/images/product/" + i + ".png");
            dto.setInStock(100);
            dto.setSoldQuantity(10 * i);
            dto.setTransactionDate(LocalDate.now().minusDays(i));
            dto.setTotalAmount(dto.getPrice().multiply(BigDecimal.valueOf(dto.getSoldQuantity())));
            bestSellingProducts.add(dto);
        }
        dashboardData.setBestSellingProducts(bestSellingProducts);
        
        // Giao dịch gần đây
        List<TransactionSummaryDTO> recentTransactions = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            TransactionSummaryDTO dto = new TransactionSummaryDTO();
            dto.setId((long) i);
            dto.setInvoiceNumber("INV-2023-" + (1000 + i));
            dto.setTransactionDate(new java.sql.Timestamp(System.currentTimeMillis() - i * 86400000));
            dto.setAmount(BigDecimal.valueOf(1000000 * i));
            dto.setPaid(i % 2 == 0);
            recentTransactions.add(dto);
        }
        dashboardData.setRecentTransactions(recentTransactions);
        
        // Giao dịch tài chính
        List<Map<String, Object>> financialTransactions = new ArrayList<>();
        String[] types = {"Tiền mặt", "Chuyển khoản", "Thẻ tín dụng"};
        String[] descriptions = {"Thanh toán cho nhà cung cấp", "Nhận thanh toán từ bảo hiểm", "Hoàn tiền"};
        String[] icons = {"ri-shield-line", "ri-check-line", "ri-exchange-dollar-line", "ri-bank-card-line", "ri-bar-chart-grouped-line"};
        String[] colors = {"", "td-color-1", "td-color-2", "td-color-3", "td-color-4"};
        
        for (int i = 0; i < 5; i++) {
            Map<String, Object> transaction = new HashMap<>();
            transaction.put("type", types[i % types.length]);
            transaction.put("description", descriptions[i % descriptions.length]);
            transaction.put("amount", BigDecimal.valueOf((i % 2 == 0 ? -1 : 1) * (1000000 + i * 500000)));
            transaction.put("icon", icons[i % icons.length]);
            transaction.put("colorClass", colors[i % colors.length]);
            financialTransactions.add(transaction);
        }
        dashboardData.setFinancialTransactions(financialTransactions);
        
        // Danh sách công việc
        List<Map<String, Object>> todoList = new ArrayList<>();
        String[] tasks = {"Kiểm kê kho thuốc", "Chuẩn bị báo cáo tháng", "Tạo hóa đơn", "Cuộc họp với nhà cung cấp"};
        int[] hours = {8, 3, 1, 5};
        
        for (int i = 0; i < tasks.length; i++) {
            Map<String, Object> task = new HashMap<>();
            task.put("name", tasks[i]);
            task.put("hours", hours[i]);
            todoList.add(task);
        }
        dashboardData.setTodoList(todoList);
        
        return dashboardData;
    }
} 