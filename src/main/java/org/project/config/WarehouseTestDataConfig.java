package org.project.config;

import org.project.entity.*;
import org.project.enums.ProductType;
import org.project.enums.StockStatus;
import org.project.enums.StockTransactionType;
import org.project.enums.UserRole;
import org.project.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Cấu hình để khởi tạo dữ liệu mẫu cho phần kho
 * Chỉ hoạt động khi profile là "test"
 */
@Configuration
@Profile("test")
public class WarehouseTestDataConfig {

    /**
     * Khởi tạo dữ liệu mẫu cho phần kho
     */
    @Bean
    public CommandLineRunner initWarehouseData(
            ProductRepository productRepository,
            SupplierRepository supplierRepository,
            StockRequestRepository stockRequestRepository,
            StockRequestItemRepository stockRequestItemRepository,
            StockInvoiceRepository stockInvoiceRepository,
            UserRepository userRepository,
            InventoryManagerRepository inventoryManagerRepository) {
        
        return args -> {
            System.out.println("Đang khởi tạo dữ liệu mẫu cho phần kho...");
            
            // Tạo user mẫu
            UserEntity warehouseManager = new UserEntity();
            warehouseManager.setEmail("warehouse@example.com");
            warehouseManager.setPasswordHash("$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG"); // password: 123456
            warehouseManager.setPhoneNumber("0123456789");
            warehouseManager.setUserRole(UserRole.STAFF); // Sử dụng STAFF vì không có WAREHOUSE_MANAGER
            warehouseManager.setIsVerified(true);
            userRepository.save(warehouseManager);
            
            // Tạo inventory manager
            InventoryManagerEntity inventoryManager = new InventoryManagerEntity();
            inventoryManager.setId(1L);
            inventoryManagerRepository.save(inventoryManager);
            
            // Tạo nhà cung cấp mẫu
            SupplierEntity supplier1 = new SupplierEntity();
            supplier1.setName("Nhà cung cấp thiết bị y tế ABC");
            supplier1.setEmail("abc@supplier.com");
            supplier1.setPhoneNumber("0123456789");
            supplierRepository.save(supplier1);
            
            SupplierEntity supplier2 = new SupplierEntity();
            supplier2.setName("Công ty Dược phẩm XYZ");
            supplier2.setEmail("xyz@supplier.com");
            supplier2.setPhoneNumber("0987654321");
            supplierRepository.save(supplier2);
            
            // Tạo sản phẩm mẫu - thiết bị y tế
            ProductEntity product1 = new ProductEntity();
            product1.setName("Máy đo huyết áp");
            product1.setDescription("Máy đo huyết áp tự động");
            product1.setPrice(new BigDecimal("1500000.0"));
            product1.setUnit("Cái");
            product1.setStockQuantities(10);
            product1.setProductType(ProductType.MEDICAL_PRODUCT);
            productRepository.save(product1);
            
            ProductEntity product2 = new ProductEntity();
            product2.setName("Nhiệt kế điện tử");
            product2.setDescription("Nhiệt kế điện tử đo nhanh");
            product2.setPrice(new BigDecimal("200000.0"));
            product2.setUnit("Cái");
            product2.setStockQuantities(50);
            product2.setProductType(ProductType.MEDICAL_PRODUCT);
            productRepository.save(product2);
            
            // Tạo sản phẩm mẫu - thuốc
            ProductEntity product3 = new ProductEntity();
            product3.setName("Paracetamol");
            product3.setDescription("Thuốc giảm đau, hạ sốt");
            product3.setPrice(new BigDecimal("15000.0"));
            product3.setUnit("Hộp");
            product3.setStockQuantities(100);
            product3.setProductType(ProductType.MEDICINE);
            productRepository.save(product3);
            
            ProductEntity product4 = new ProductEntity();
            product4.setName("Amoxicillin");
            product4.setDescription("Kháng sinh phổ rộng");
            product4.setPrice(new BigDecimal("25000.0"));
            product4.setUnit("Hộp");
            product4.setStockQuantities(80);
            product4.setProductType(ProductType.MEDICINE);
            productRepository.save(product4);
            
            // Tạo yêu cầu nhập kho mẫu
            StockRequestEntity stockInRequest = new StockRequestEntity();
            stockInRequest.setTransactionType(StockTransactionType.STOCK_IN);
            stockInRequest.setStatus(StockStatus.PENDING);
            stockInRequest.setRequestDate(Timestamp.valueOf(LocalDateTime.now()));
            stockInRequest.setExpectedDeliveryDate(Timestamp.valueOf(LocalDateTime.now().plusDays(7)));
            stockInRequest.setSupplier(supplier1);
            stockInRequest.setRequestedBy(inventoryManager);
            stockRequestRepository.save(stockInRequest);
            
            // Tạo các item cho yêu cầu nhập kho
            StockRequestItemEntity stockInItem1 = new StockRequestItemEntity();
            StockRequestItemEntityId stockInItemId1 = new StockRequestItemEntityId();
            stockInItemId1.setStockRequestId(stockInRequest.getId());
            stockInItemId1.setProductId(product1.getId());
            stockInItem1.setId(stockInItemId1);
            stockInItem1.setStockRequest(stockInRequest);
            stockInItem1.setProduct(product1);
            stockInItem1.setQuantity(5);
            stockInItem1.setUnitPrice(new BigDecimal("1400000.0")); // Giá nhập
            stockRequestItemRepository.save(stockInItem1);
            
            StockRequestItemEntity stockInItem2 = new StockRequestItemEntity();
            StockRequestItemEntityId stockInItemId2 = new StockRequestItemEntityId();
            stockInItemId2.setStockRequestId(stockInRequest.getId());
            stockInItemId2.setProductId(product2.getId());
            stockInItem2.setId(stockInItemId2);
            stockInItem2.setStockRequest(stockInRequest);
            stockInItem2.setProduct(product2);
            stockInItem2.setQuantity(20);
            stockInItem2.setUnitPrice(new BigDecimal("180000.0")); // Giá nhập
            stockRequestItemRepository.save(stockInItem2);
            
            // Tạo yêu cầu xuất kho mẫu
            StockRequestEntity stockOutRequest = new StockRequestEntity();
            stockOutRequest.setTransactionType(StockTransactionType.STOCK_OUT);
            stockOutRequest.setStatus(StockStatus.PENDING);
            stockOutRequest.setRequestDate(Timestamp.valueOf(LocalDateTime.now()));
            stockOutRequest.setExpectedDeliveryDate(Timestamp.valueOf(LocalDateTime.now().plusDays(3)));
            stockOutRequest.setRequestedBy(inventoryManager);
            stockRequestRepository.save(stockOutRequest);
            
            // Tạo các item cho yêu cầu xuất kho
            StockRequestItemEntity stockOutItem1 = new StockRequestItemEntity();
            StockRequestItemEntityId stockOutItemId1 = new StockRequestItemEntityId();
            stockOutItemId1.setStockRequestId(stockOutRequest.getId());
            stockOutItemId1.setProductId(product3.getId());
            stockOutItem1.setId(stockOutItemId1);
            stockOutItem1.setStockRequest(stockOutRequest);
            stockOutItem1.setProduct(product3);
            stockOutItem1.setQuantity(10);
            stockOutItem1.setUnitPrice(new BigDecimal("15000.0"));
            stockRequestItemRepository.save(stockOutItem1);
            
            StockRequestItemEntity stockOutItem2 = new StockRequestItemEntity();
            StockRequestItemEntityId stockOutItemId2 = new StockRequestItemEntityId();
            stockOutItemId2.setStockRequestId(stockOutRequest.getId());
            stockOutItemId2.setProductId(product4.getId());
            stockOutItem2.setId(stockOutItemId2);
            stockOutItem2.setStockRequest(stockOutRequest);
            stockOutItem2.setProduct(product4);
            stockOutItem2.setQuantity(5);
            stockOutItem2.setUnitPrice(new BigDecimal("25000.0"));
            stockRequestItemRepository.save(stockOutItem2);
            
            // Tạo một hóa đơn nhập kho mẫu (đã hoàn thành)
            StockRequestEntity completedStockInRequest = new StockRequestEntity();
            completedStockInRequest.setTransactionType(StockTransactionType.STOCK_IN);
            completedStockInRequest.setStatus(StockStatus.COMPLETED);
            completedStockInRequest.setRequestDate(Timestamp.valueOf(LocalDateTime.now().minusDays(14)));
            completedStockInRequest.setExpectedDeliveryDate(Timestamp.valueOf(LocalDateTime.now().minusDays(7)));
            completedStockInRequest.setApprovedDate(Timestamp.valueOf(LocalDateTime.now().minusDays(7)));
            completedStockInRequest.setSupplier(supplier2);
            completedStockInRequest.setRequestedBy(inventoryManager);
            completedStockInRequest.setApprovedBy(inventoryManager);
            stockRequestRepository.save(completedStockInRequest);
            
            // Tạo các item cho yêu cầu nhập kho đã hoàn thành
            StockRequestItemEntity completedStockInItem = new StockRequestItemEntity();
            StockRequestItemEntityId completedStockInItemId = new StockRequestItemEntityId();
            completedStockInItemId.setStockRequestId(completedStockInRequest.getId());
            completedStockInItemId.setProductId(product4.getId());
            completedStockInItem.setId(completedStockInItemId);
            completedStockInItem.setStockRequest(completedStockInRequest);
            completedStockInItem.setProduct(product4);
            completedStockInItem.setQuantity(50);
            completedStockInItem.setUnitPrice(new BigDecimal("23000.0")); // Giá nhập
            stockRequestItemRepository.save(completedStockInItem);
            
            // Tạo hóa đơn nhập kho
            StockInvoiceEntity stockInInvoice = new StockInvoiceEntity();
            stockInInvoice.setStockRequest(completedStockInRequest);
            stockInInvoice.setInvoiceNumber("INV-" + System.currentTimeMillis());
            stockInInvoice.setTransactionType(StockTransactionType.STOCK_IN);
            stockInInvoice.setInvoiceDate(Timestamp.valueOf(LocalDateTime.now().minusDays(7)));
            stockInInvoice.setTotalAmount(new BigDecimal("1150000.0")); // 50 * 23000.0
            stockInInvoice.setStatus(StockStatus.COMPLETED);
            stockInInvoice.setCreatedBy(inventoryManager);
            stockInvoiceRepository.save(stockInInvoice);
            
            System.out.println("Dữ liệu mẫu cho phần kho đã được tạo thành công!");
        };
    }
} 