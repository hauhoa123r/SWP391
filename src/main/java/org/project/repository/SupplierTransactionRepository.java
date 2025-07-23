package org.project.repository;

import org.project.entity.SupplierTransactionsEntity;
import org.project.enums.SupplierTransactionStatus;
import org.project.enums.SupplierTransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface SupplierTransactionRepository extends JpaRepository<SupplierTransactionsEntity, Long> {
    Page<SupplierTransactionsEntity> findByStatus(SupplierTransactionStatus status, Pageable pageable);
    Page<SupplierTransactionsEntity> findByTransactionType(SupplierTransactionType type, Pageable pageable);
    Page<SupplierTransactionsEntity> findByTransactionDateBetween(Timestamp startDate, Timestamp endDate, Pageable pageable);
    List<SupplierTransactionsEntity> findBySupplierEntityId(Long supplierId);
    List<SupplierTransactionsEntity> findByInvoiceNumberStartingWith(String invoiceNumber);
    
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
}