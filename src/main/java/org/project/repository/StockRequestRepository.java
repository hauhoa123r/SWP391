package org.project.repository;

import org.project.entity.StockRequestEntity;
import org.project.enums.StockStatus;
import org.project.enums.StockTransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface StockRequestRepository extends JpaRepository<StockRequestEntity, Long> {
    
    Page<StockRequestEntity> findByTransactionType(StockTransactionType transactionType, Pageable pageable);
    
    Page<StockRequestEntity> findByStatus(StockStatus status, Pageable pageable);
    
    Page<StockRequestEntity> findByTransactionTypeAndStatus(StockTransactionType transactionType, StockStatus status, Pageable pageable);
    
    @Query("SELECT sr FROM StockRequestEntity sr WHERE sr.requestDate BETWEEN :startDate AND :endDate")
    Page<StockRequestEntity> findByDateRange(@Param("startDate") Timestamp startDate, @Param("endDate") Timestamp endDate, Pageable pageable);
    
    @Query("SELECT sr FROM StockRequestEntity sr WHERE sr.transactionType = :transactionType AND sr.requestDate BETWEEN :startDate AND :endDate")
    Page<StockRequestEntity> findByTransactionTypeAndDateRange(
            @Param("transactionType") StockTransactionType transactionType,
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate,
            Pageable pageable);
    
    List<StockRequestEntity> findBySupplier_Id(Long supplierId);
    
    List<StockRequestEntity> findByRequestedBy_Id(Long inventoryManagerId);
    
    @Query("SELECT sr FROM StockRequestEntity sr JOIN sr.stockRequestItems sri WHERE sri.product.id = :productId")
    List<StockRequestEntity> findByProductId(@Param("productId") Long productId);
} 