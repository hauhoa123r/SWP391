        package org.project.repository;

import org.project.entity.SupplierTransactionsEntity;
import org.project.enums.SupplierTransactionStatus;
import org.project.enums.SupplierTransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierTransactionRepository extends JpaRepository<SupplierTransactionsEntity, Long> {
    Page<SupplierTransactionsEntity> findByStatus(SupplierTransactionStatus status, Pageable pageable);
    Page<SupplierTransactionsEntity> findByTransactionType(SupplierTransactionType type, Pageable pageable);
    Page<SupplierTransactionsEntity> findByTransactionDateBetween(Timestamp startDate, Timestamp endDate, Pageable pageable);
    List<SupplierTransactionsEntity> findBySupplierEntityId(Long supplierId);
    List<SupplierTransactionsEntity> findByInvoiceNumberStartingWith(String invoiceNumber);
    List<SupplierTransactionsEntity> findTop4ByOrderByTransactionDateDesc();

    // Thêm các phương thức mới
    List<SupplierTransactionsEntity> findByTransactionType(SupplierTransactionType type);
    Page<SupplierTransactionsEntity> findByTransactionTypeAndStatusAndInvoiceNumberContaining(
            SupplierTransactionType type, SupplierTransactionStatus status, String invoiceNumber, Pageable pageable);
    Page<SupplierTransactionsEntity> findByTransactionTypeAndInvoiceNumberContaining(
            SupplierTransactionType type, String invoiceNumber, Pageable pageable);
    Page<SupplierTransactionsEntity> findByTransactionTypeAndStatus(
            SupplierTransactionType type, SupplierTransactionStatus status, Pageable pageable);
    List<SupplierTransactionsEntity> findTop10ByOrderByTransactionDateDesc();
    List<SupplierTransactionsEntity> findByTransactionTypeAndTransactionDateBetween(SupplierTransactionType type,
            Timestamp startTs, Timestamp endTs);
    @Query("SELECT SUM(i.quantity) FROM SupplierTransactionItemEntity i WHERE i.productEntity.id = :productId AND i.supplierTransactionEntity.transactionType = org.project.enums.SupplierTransactionType.STOCK_OUT")
    Integer findSoldQuantityByProductId(@Param("productId") Long productId);

    @Query("SELECT SUM(t.totalAmount) FROM SupplierTransactionsEntity t")
    BigDecimal findTotalRevenue();

    // New methods for filtering by supplier name and status
    Page<SupplierTransactionsEntity> findByTransactionTypeAndStatusAndSupplierEntityNameContainingIgnoreCase(
            SupplierTransactionType type, SupplierTransactionStatus status, String supplierName, Pageable pageable);

    // Thêm phương thức mới để tìm kiếm theo tên nhà cung cấp mà không quan tâm đến trạng thái
    Page<SupplierTransactionsEntity> findByTransactionTypeAndSupplierEntityNameContainingIgnoreCase(
            SupplierTransactionType type, String supplierName, Pageable pageable);

    Page<SupplierTransactionsEntity> findByTransactionTypeAndStatusNotInAndSupplierEntityNameContainingIgnoreCase(
            SupplierTransactionType type, Collection<SupplierTransactionStatus> statuses, String supplierName, Pageable pageable);

    Page<SupplierTransactionsEntity> findByTransactionTypeAndStatusNotIn(
            SupplierTransactionType type, Collection<SupplierTransactionStatus> statuses, Pageable pageable);

    // Methods for filtering by a list of allowed statuses
    Page<SupplierTransactionsEntity> findByTransactionTypeAndStatusIn(
            SupplierTransactionType type, Collection<SupplierTransactionStatus> statuses, Pageable pageable);

    Page<SupplierTransactionsEntity> findByTransactionTypeAndStatusInAndSupplierEntityNameContainingIgnoreCase(
            SupplierTransactionType type, Collection<SupplierTransactionStatus> statuses, String supplierName, Pageable pageable);

    // Methods for filtering by invoice number with statuses for StockInInvoice and StockOutInvoice
    Page<SupplierTransactionsEntity> findByTransactionTypeAndStatusInAndInvoiceNumberContaining(
            SupplierTransactionType type, Collection<SupplierTransactionStatus> statuses, String invoiceNumber, Pageable pageable);

    Optional<SupplierTransactionsEntity> findById(Long id);

    @Query("SELECT t FROM SupplierTransactionsEntity t WHERE t.transactionType = :type AND t.transactionDate BETWEEN :startDate AND :endDate")
    Page<SupplierTransactionsEntity> findByTransactionTypeAndDateRange(@Param("type") SupplierTransactionType type, @Param("startDate") Timestamp startDate, @Param("endDate") Timestamp endDate, Pageable pageable);

    @Query("SELECT t FROM SupplierTransactionsEntity t WHERE t.transactionType = :type AND (t.invoiceNumber LIKE %:keyword% OR t.notes LIKE %:keyword%)")
    Page<SupplierTransactionsEntity> findByTransactionTypeAndKeyword(@Param("type") SupplierTransactionType type, @Param("keyword") String keyword, Pageable pageable);

    Page<SupplierTransactionsEntity> findByTransactionTypeAndInvoiceNumberContainingIgnoreCase(SupplierTransactionType type, String invoiceNumber, Pageable pageable);

    @Query("SELECT t FROM SupplierTransactionsEntity t WHERE t.transactionType = :type AND (:status IS NULL OR t.status = :status) AND (:start IS NULL OR t.transactionDate >= :start) AND (:end IS NULL OR t.transactionDate <= :end)")
    Page<SupplierTransactionsEntity> findByTransactionTypeAndStatusAndTransactionDateBetween(@Param("type") SupplierTransactionType type, @Param("status") SupplierTransactionStatus status, @Param("start") Timestamp start, @Param("end") Timestamp end, Pageable pageable);

    @Query("SELECT t FROM SupplierTransactionsEntity t WHERE t.transactionType = :type AND (t.status = 'COMPLETED' OR t.status = 'REJECTED')")
    List<SupplierTransactionsEntity> findCompletedOrRejectedInvoicesByType(@Param("type") SupplierTransactionType type);
    
    List<SupplierTransactionsEntity> findByStatus(SupplierTransactionStatus status);
    
    Page<SupplierTransactionsEntity> findAll(Specification<SupplierTransactionsEntity> spec, Pageable pageable);
}