package org.project.repository;

import org.project.entity.SupplierInvoiceEntity;
import org.project.entity.SupplierTransactionsEntity;
import org.project.enums.SupplierTransactionStatus;
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
            
    // Thêm phương thức truy vấn native SQL chỉ lấy các đơn có trạng thái COMPLETED
    @Query(value = "SELECT * FROM supplier_invoices WHERE transaction_type = :type AND status = 'COMPLETED'", 
           nativeQuery = true)
    List<SupplierInvoiceEntity> findCompletedInvoicesByType(@Param("type") String type);
    
    // Thêm phương thức truy vấn JPQL lấy các đơn có trạng thái COMPLETED hoặc REJECTED
    @Query("SELECT s FROM SupplierInvoiceEntity s WHERE s.transactionType = :type AND (s.status = org.project.enums.SupplierTransactionStatus.COMPLETED OR s.status = org.project.enums.SupplierTransactionStatus.REJECTED)")
    List<SupplierInvoiceEntity> findCompletedOrRejectedInvoicesByType(@Param("type") org.project.enums.SupplierTransactionType type);

    // In SupplierTransactionRepository
    @Query("SELECT t FROM SupplierTransactionsEntity t LEFT JOIN FETCH t.supplierTransactionItemEntities items LEFT JOIN FETCH items.productEntity p WHERE t.id = :id")
    Optional<SupplierTransactionsEntity> findWithItemsAndProductsById(@Param("id") Long id);
}