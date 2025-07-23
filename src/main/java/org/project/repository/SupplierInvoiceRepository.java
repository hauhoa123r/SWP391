package org.project.repository;

import org.project.entity.SupplierInvoiceEntity;
import org.project.enums.SupplierTransactionStatus;
import org.project.enums.SupplierTransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierInvoiceRepository extends JpaRepository<SupplierInvoiceEntity, Long> {
    Optional<SupplierInvoiceEntity> findByInvoiceNumber(String invoiceNumber);
    Page<SupplierInvoiceEntity> findByTransactionType(SupplierTransactionType transactionType, Pageable pageable);
    Page<SupplierInvoiceEntity> findByInvoiceDateBetween(Timestamp startDate, Timestamp endDate, Pageable pageable);
    Page<SupplierInvoiceEntity> findByStatus(SupplierTransactionStatus status, Pageable pageable);
    List<SupplierInvoiceEntity> findByCreatedById(Long managerId);
    
    // Thêm các phương thức mới
    List<SupplierInvoiceEntity> findByTransactionType(SupplierTransactionType type);
    Page<SupplierInvoiceEntity> findByTransactionTypeAndStatusAndInvoiceNumberContaining(
            SupplierTransactionType type, SupplierTransactionStatus status, String invoiceNumber, Pageable pageable);
    Page<SupplierInvoiceEntity> findByTransactionTypeAndInvoiceNumberContaining(
            SupplierTransactionType type, String invoiceNumber, Pageable pageable);
    Page<SupplierInvoiceEntity> findByTransactionTypeAndStatus(
            SupplierTransactionType type, SupplierTransactionStatus status, Pageable pageable);
}