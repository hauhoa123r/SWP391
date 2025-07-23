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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
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
    
    @GetMapping({"/", "/index.html"})
    public String dashboard(Model model) {
        log.info("Loading dashboard data");
        
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
            
            // Top sản phẩm bán chạy
            List<ProductEntity> topProducts = productRepository.findTopSellingProducts(PageRequest.of(0, 3));
            List<ProductSummaryDTO> bestSellingProducts = new ArrayList<>();
            
            if (topProducts != null && !topProducts.isEmpty()) {
                bestSellingProducts = topProducts.stream().map(product -> {
                    ProductSummaryDTO dto = new ProductSummaryDTO();
                    dto.setId(product.getId());
                    dto.setName(product.getName());
                    dto.setPrice(product.getPrice());
                    dto.setImageUrl(product.getImageUrl());
                    dto.setInStock(product.getStockQuantities());
                    
                    // Lấy số lượng đã bán
                    Integer soldQuantity = transactionRepository.findSoldQuantityByProductId(product.getId());
                    dto.setSoldQuantity(soldQuantity != null ? soldQuantity : 0);
                    
                    // Ngày giao dịch giả lập
                    dto.setTransactionDate(LocalDate.now().minusDays((long) (Math.random() * 10)));
                    
                    // Tính tổng tiền
                    dto.setTotalAmount(product.getPrice().multiply(BigDecimal.valueOf(dto.getSoldQuantity())));
                    return dto;
                }).collect(Collectors.toList());
            } else {
                // Tạo dữ liệu mẫu nếu không có dữ liệu thật
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
            }
            
            // Giao dịch gần đây
            List<SupplierTransactionsEntity> recentTransactions = transactionRepository.findTop4ByOrderByTransactionDateDesc();
            List<TransactionSummaryDTO> recentTransactionsList = new ArrayList<>();
            
            if (recentTransactions != null && !recentTransactions.isEmpty()) {
                recentTransactionsList = recentTransactions.stream().map(transaction -> {
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
            } else {
                // Tạo dữ liệu mẫu nếu không có dữ liệu thật
                String[] types = {"Nhập kho", "Xuất kho"};
                String[] statuses = {"Hoàn thành", "Đang xử lý", "Chờ duyệt", "Đã hủy"};
                
                for (int i = 1; i <= 4; i++) {
                    TransactionSummaryDTO dto = new TransactionSummaryDTO();
                    dto.setId((long) i);
                    dto.setInvoiceNumber("INV-2023-" + (1000 + i));
                    // Sử dụng null thay vì enum vì đây là dữ liệu mẫu
                    dto.setTransactionType(null);
                    dto.setTransactionDate(new java.sql.Timestamp(System.currentTimeMillis() - i * 86400000));
                    dto.setAmount(BigDecimal.valueOf(1000000 * i));
                    // Sử dụng null thay vì enum vì đây là dữ liệu mẫu
                    dto.setStatus(null);
                    dto.setPaid(i % 2 == 0);
                    recentTransactionsList.add(dto);
                }
            }
            
            // Giao dịch tài chính
            List<Map<String, Object>> financialTransactions = new ArrayList<>();
            
            Map<String, Object> transaction1 = new HashMap<>();
            transaction1.put("type", "Tiền mặt");
            transaction1.put("description", "Thanh toán cho nhà cung cấp");
            transaction1.put("amount", BigDecimal.valueOf(-5750000));
            transaction1.put("icon", "ri-shield-line");
            transaction1.put("colorClass", "");
            financialTransactions.add(transaction1);
            
            Map<String, Object> transaction2 = new HashMap<>();
            transaction2.put("type", "Chuyển khoản");
            transaction2.put("description", "Nhận thanh toán từ bảo hiểm");
            transaction2.put("amount", BigDecimal.valueOf(12500000));
            transaction2.put("icon", "ri-check-line");
            transaction2.put("colorClass", "td-color-1");
            financialTransactions.add(transaction2);
            
            Map<String, Object> transaction3 = new HashMap<>();
            transaction3.put("type", "Tiền mặt");
            transaction3.put("description", "Thanh toán cho nhà cung cấp");
            transaction3.put("amount", BigDecimal.valueOf(-3200000));
            transaction3.put("icon", "ri-exchange-dollar-line");
            transaction3.put("colorClass", "td-color-2");
            financialTransactions.add(transaction3);
            
            Map<String, Object> transaction4 = new HashMap<>();
            transaction4.put("type", "Thẻ tín dụng");
            transaction4.put("description", "Thanh toán cho nhà cung cấp");
            transaction4.put("amount", BigDecimal.valueOf(-4900000));
            transaction4.put("icon", "ri-bank-card-line");
            transaction4.put("colorClass", "td-color-3");
            financialTransactions.add(transaction4);
            
            Map<String, Object> transaction5 = new HashMap<>();
            transaction5.put("type", "Chuyển khoản");
            transaction5.put("description", "Hoàn tiền");
            transaction5.put("amount", BigDecimal.valueOf(1200000));
            transaction5.put("icon", "ri-bar-chart-grouped-line");
            transaction5.put("colorClass", "td-color-4");
            financialTransactions.add(transaction5);
            
            // Danh sách công việc
            List<Map<String, Object>> todoList = new ArrayList<>();
            
            Map<String, Object> task1 = new HashMap<>();
            task1.put("name", "Kiểm kê kho thuốc");
            task1.put("hours", 8);
            todoList.add(task1);
            
            Map<String, Object> task2 = new HashMap<>();
            task2.put("name", "Chuẩn bị báo cáo tháng");
            task2.put("hours", 3);
            todoList.add(task2);
            
            Map<String, Object> task3 = new HashMap<>();
            task3.put("name", "Tạo hóa đơn");
            task3.put("hours", 1);
            todoList.add(task3);
            
            Map<String, Object> task4 = new HashMap<>();
            task4.put("name", "Cuộc họp với nhà cung cấp");
            task4.put("hours", 5);
            todoList.add(task4);
            
            // Tạo DTO tổng hợp cho dashboard
            DashboardDTO dashboardData = new DashboardDTO();
            dashboardData.setTotalProducts(totalProducts);
            dashboardData.setTotalTransactions(totalTransactions);
            dashboardData.setTotalRevenue(totalRevenue);
            dashboardData.setTotalPatients(totalPatients);
            dashboardData.setBestSellingProducts(bestSellingProducts);
            dashboardData.setRecentTransactions(recentTransactionsList);
            dashboardData.setFinancialTransactions(financialTransactions);
            dashboardData.setTodoList(todoList);
            
            // Thêm biến thống kê vào model
            model.addAttribute("dashboard", dashboardData);
            
        } catch (Exception e) {
            log.error("Error loading dashboard data: {}", e.getMessage(), e);
            // Tạo dữ liệu mẫu nếu có lỗi
            DashboardDTO dashboardData = createSampleDashboardData();
            model.addAttribute("dashboard", dashboardData);
            model.addAttribute("error", "Có lỗi khi tải dữ liệu dashboard: " + e.getMessage());
        }
        
        return "templates_storage/index";
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