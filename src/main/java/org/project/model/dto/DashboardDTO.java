package org.project.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * DTO chứa dữ liệu tổng hợp cho trang dashboard
 */
@Data
public class DashboardDTO {
    // Dữ liệu tổng quan
    private long totalProducts;
    private long totalTransactions;
    private BigDecimal totalRevenue;
    private int totalPatients;
    
    // Phần trăm tăng trưởng
    private double revenueGrowthPercent = 8.5;
    private double transactionGrowthPercent = 12.3;
    private double patientGrowthPercent = 5.2;
    
    // Dữ liệu chi tiết
    private List<ProductSummaryDTO> bestSellingProducts;
    private List<TransactionSummaryDTO> recentTransactions;
    private List<Map<String, Object>> financialTransactions;
    private List<Map<String, Object>> todoList;
} 