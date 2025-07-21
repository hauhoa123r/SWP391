package org.project.repository;

import org.project.entity.StockInvoiceEntity;
import org.project.enums.StockStatus;
import org.project.enums.SupplierTransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockInvoiceRepository extends JpaRepository<StockInvoiceEntity, Long> {
    
    Optional<StockInvoiceEntity> findByInvoiceNumber(String invoiceNumber);
    
    Optional<StockInvoiceEntity> findByStockRequest_Id(Long stockRequestId);
    
    Page<StockInvoiceEntity> findByTransactionType(SupplierTransactionType transactionType, Pageable pageable);
    
    Page<StockInvoiceEntity> findByStatus(StockStatus status, Pageable pageable);
    
    Page<StockInvoiceEntity> findByTransactionTypeAndStatus(SupplierTransactionType transactionType, StockStatus status, Pageable pageable);
    
    @Query("SELECT si FROM StockInvoiceEntity si WHERE si.invoiceDate BETWEEN :startDate AND :endDate")
    Page<StockInvoiceEntity> findByDateRange(@Param("startDate") Timestamp startDate, @Param("endDate") Timestamp endDate, Pageable pageable);
    
    @Query("SELECT si FROM StockInvoiceEntity si WHERE si.transactionType = :transactionType AND si.invoiceDate BETWEEN :startDate AND :endDate")
    Page<StockInvoiceEntity> findByTransactionTypeAndDateRange(
            @Param("transactionType") SupplierTransactionType transactionType,
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate,
            Pageable pageable);
    
    List<StockInvoiceEntity> findByCreatedBy_Id(Long inventoryManagerId);
    
    @Query("SELECT si FROM StockInvoiceEntity si JOIN si.stockRequest sr JOIN sr.stockRequestItems sri WHERE sri.product.id = :productId")
    List<StockInvoiceEntity> findByProductId(@Param("productId") Long productId);
} 