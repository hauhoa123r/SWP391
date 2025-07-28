    package org.project.service;

import org.project.entity.SupplierTransactionsEntity;
import org.project.enums.SupplierTransactionStatus;
import org.project.model.dto.SupplierInDTO;
import org.project.model.dto.SupplierInvoiceDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.util.List;

public interface SupplierInInvoiceService {

    SupplierInvoiceDTO saveInvoice(SupplierInDTO supplierInDTO);

    
    Page<SupplierInvoiceDTO> getAllInvoices(Pageable pageable);

    Page<SupplierInvoiceDTO> getFilteredInvoices(Pageable pageable, String keyword, String status, String startDateStr, String endDateStr);

    Page<SupplierInvoiceDTO> getFilteredSupplierInsForStockIn(Pageable pageable, String keyword, String startDateStr, String endDateStr, List<SupplierTransactionStatus> statusList);

    void rejectInvoice(Long id, String rejectionReason);

    void saveInvoiceWithRejection(SupplierInDTO supplierIn, String reason);
}