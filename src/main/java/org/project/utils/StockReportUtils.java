package org.project.utils;

import org.project.entity.ProductEntity;
import org.project.entity.StockRequestItemEntity;
import org.project.entity.SupplierTransactionItemEntity;
import org.project.entity.SupplierTransactionsEntity;
import org.project.enums.SupplierTransactionType;
import org.project.model.response.ProductStockReportResponse;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

public class StockReportUtils {
    
    /**
     * Generates a report of products with low stock (below threshold)
     * 
     * @param products List of products to check
     * @param threshold Threshold below which products are considered low stock
     * @return List of products with low stock
     */
    public static List<ProductStockReportResponse> generateLowStockReport(List<ProductEntity> products, int threshold) {
        return products.stream()
                .filter(product -> product.getStockQuantities() <= threshold)
                .map(product -> {
                    ProductStockReportResponse report = new ProductStockReportResponse();
                    report.setProductId(product.getId());
                    report.setProductName(product.getName());
                    report.setCurrentStock(product.getStockQuantities());
                    report.setThreshold(threshold);
                    report.setStatus("Low Stock");
                    report.setRecommendedAction("Restock");
                    return report;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Generates a report of products with expiring stock
     * 
     * @param stockItems List of stock items to check
     * @param daysThreshold Number of days before expiration to include in report
     * @return List of products with expiring stock
     */
    public static List<ProductStockReportResponse> generateExpiringStockReport(
            List<StockRequestItemEntity> stockItems, int daysThreshold) {
        
        Date thresholdDate = new Date(System.currentTimeMillis() + (long) daysThreshold * 24 * 60 * 60 * 1000);
        
        return stockItems.stream()
                .filter(item -> item.getExpirationDate() != null && 
                        item.getExpirationDate().before(thresholdDate))
                .map(item -> {
                    ProductStockReportResponse report = new ProductStockReportResponse();
                    report.setProductId(item.getProduct().getId());
                    report.setProductName(item.getProduct().getName());
                    report.setCurrentStock(item.getProduct().getStockQuantities());
                    report.setExpirationDate(item.getExpirationDate());
                    report.setStatus("Expiring Soon");
                    report.setRecommendedAction("Use or Discard");
                    return report;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Generates a report of product movement (transactions) during a date range
     * 
     * @param transactions List of stock requests within the date range
     * @param startDate Start date of the report period
     * @param endDate End date of the report period
     * @return Map of product IDs to their stock movement data
     */
    public static Map<Long, ProductStockReportResponse> generateStockMovementReport(
            List<SupplierTransactionsEntity> transactions, Timestamp startDate, Timestamp endDate) {
        
        Map<Long, ProductStockReportResponse> reportMap = new HashMap<>();
        
        for (SupplierTransactionsEntity transaction : transactions) {
            if (transaction.getTransactionDate().after(startDate) && transaction.getTransactionDate().before(endDate)) {
                for (SupplierTransactionItemEntity item : transaction.getSupplierTransactionItemEntities()) {
                    ProductEntity product = item.getProductEntity();
                    if (product == null) continue;
                    
                    Long productId = product.getId();
                    
                    ProductStockReportResponse report = reportMap.getOrDefault(productId, new ProductStockReportResponse());
                    report.setProductId(productId);
                    report.setProductName(product.getName());
                    report.setCurrentStock(product.getStockQuantities());
                    
                    int quantity = item.getQuantity();
                    if (transaction.getTransactionType() == SupplierTransactionType.STOCK_IN) {
                        report.setStockIn(report.getStockIn() + quantity);
                    } else {
                        report.setStockOut(report.getStockOut() + quantity);
                    }
                    
                    BigDecimal value = item.getUnitPrice() != null ? 
                            item.getUnitPrice().multiply(BigDecimal.valueOf(quantity)) : BigDecimal.ZERO;
                    
                    if (transaction.getTransactionType() == SupplierTransactionType.STOCK_IN) {
                        report.setStockInValue(report.getStockInValue().add(value));
                    } else {
                        report.setStockOutValue(report.getStockOutValue().add(value));
                    }
                    
                    reportMap.put(productId, report);
                }
            }
        }
        
        return reportMap;
    }
    
    /**
     * Generates a stock valuation report for all products
     * 
     * @param products List of products to include in the report
     * @param stockItems List of stock items with pricing information
     * @return List of products with their current valuation
     */
    public static List<ProductStockReportResponse> generateStockValuationReport(
            List<ProductEntity> products, List<StockRequestItemEntity> stockItems) {
        
        // Create a map of product ID to average unit price based on recent stock items
        Map<Long, BigDecimal> averagePriceMap = new HashMap<>();
        
        // Group stock items by product ID
        Map<Long, List<StockRequestItemEntity>> itemsByProduct = stockItems.stream()
                .filter(item -> item.getUnitPrice() != null)
                .collect(Collectors.groupingBy(item -> item.getProduct().getId()));
        
        // Calculate average price for each product
        for (Map.Entry<Long, List<StockRequestItemEntity>> entry : itemsByProduct.entrySet()) {
            Long productId = entry.getKey();
            List<StockRequestItemEntity> items = entry.getValue();
            
            OptionalDouble average = items.stream()
                    .mapToDouble(item -> item.getUnitPrice().doubleValue())
                    .average();
            
            if (average.isPresent()) {
                averagePriceMap.put(productId, BigDecimal.valueOf(average.getAsDouble()));
            }
        }
        
        // Create report entries for each product
        return products.stream()
                .map(product -> {
                    ProductStockReportResponse report = new ProductStockReportResponse();
                    report.setProductId(product.getId());
                    report.setProductName(product.getName());
                    report.setCurrentStock(product.getStockQuantities());
                    
                    BigDecimal unitPrice = averagePriceMap.getOrDefault(product.getId(), product.getPrice());
                    report.setUnitPrice(unitPrice);
                    
                    BigDecimal totalValue = unitPrice.multiply(BigDecimal.valueOf(product.getStockQuantities()));
                    report.setTotalValue(totalValue);
                    
                    return report;
                })
                .collect(Collectors.toList());
    }


} 