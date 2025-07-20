package org.project.repository;

import org.project.entity.StockRequestEntity;
import org.project.enums.SupplierTransactionType;
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
    
    @Query("SELECT s FROM StockRequestEntity s WHERE s.transactionType = :type AND s.requestDate BETWEEN :startDate AND :endDate")
    Page<StockRequestEntity> findByTransactionTypeAndDateRange(
            @Param("type") SupplierTransactionType type,
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate,
            Pageable pageable);
    
    @Query("SELECT s FROM StockRequestEntity s WHERE s.requestDate BETWEEN :startDate AND :endDate")
    Page<StockRequestEntity> findByDateRange(
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate,
            Pageable pageable);
    
    // Find by transaction type with pagination
    Page<StockRequestEntity> findByTransactionType(SupplierTransactionType type, Pageable pageable);
    
    // Find by transaction type without pagination
    List<StockRequestEntity> findByTransactionType(SupplierTransactionType type);
} 