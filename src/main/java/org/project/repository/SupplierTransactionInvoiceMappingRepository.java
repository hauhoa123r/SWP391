package org.project.repository;

import org.project.entity.SupplierTransactionInvoiceMappingEntity;
import org.project.entity.SupplierTransactionInvoiceMappingEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierTransactionInvoiceMappingRepository extends JpaRepository<SupplierTransactionInvoiceMappingEntity, SupplierTransactionInvoiceMappingEntityId> {
    List<SupplierTransactionInvoiceMappingEntity> findBySupplierTransactionEntityId(Long transactionId);
    List<SupplierTransactionInvoiceMappingEntity> findBySupplierInvoiceEntityId(Long invoiceId);
}