package org.project.service;

import org.project.entity.SupplierInvoiceEntity;
import org.project.enums.SupplierTransactionStatus;
import org.project.model.dto.SupplierInvoiceDTO;
import org.project.model.dto.SupplierOutDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.util.List;

public interface SupplierOutInvoiceService {
    List<SupplierInvoiceDTO> getAllInvoices();

    Page<SupplierInvoiceDTO> getAllInvoices(int page, int size, String keyword, String status);

    Page<SupplierInvoiceDTO> getAllInvoicesWithDateRange(Pageable pageable, String keyword, String status, Timestamp startDate, Timestamp endDate);

    SupplierInvoiceDTO getInvoiceById(Long id);

    SupplierInvoiceDTO saveInvoice(SupplierOutDTO supplierInDTO);

    SupplierInvoiceDTO updateInvoice(Long id, SupplierInvoiceDTO invoiceDTO);

    void deleteInvoice(Long id);

    SupplierInvoiceDTO updateInvoiceStatus(Long id, SupplierTransactionStatus status);

    Page<SupplierInvoiceDTO> getFilteredInvoices(int page, int size, String keyword, String status, List<SupplierTransactionStatus> allowedStatuses);
    
    /**
     * Save test invoices for debugging purposes
     * @param invoices List of test invoices to save
     */
    void saveTestInvoices(List<SupplierInvoiceEntity> invoices);
} 