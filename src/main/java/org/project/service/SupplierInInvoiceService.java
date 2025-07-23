package org.project.service;

import org.project.entity.SupplierInvoiceEntity;
import org.project.enums.SupplierTransactionStatus;
import org.project.model.dto.SupplierInDTO;
import org.project.model.dto.SupplierInvoiceDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.util.List;

public interface SupplierInInvoiceService {
    List<SupplierInvoiceDTO> getAllInvoices();
    
    Page<SupplierInvoiceDTO> getAllInvoices(int page, int size, String keyword, String status);
    
    /**
     * Get all invoices with date range filter
     * @param pageable Pagination information
     * @param keyword Optional search term
     * @param status Optional status filter
     * @param startDate Optional start date
     * @param endDate Optional end date
     * @return Paginated invoice DTOs
     */
    Page<SupplierInvoiceDTO> getAllInvoicesWithDateRange(Pageable pageable, String keyword, String status, Timestamp startDate, Timestamp endDate);
    
    SupplierInvoiceDTO getInvoiceById(Long id);
    
    SupplierInvoiceDTO saveInvoice(SupplierInDTO supplierInDTO);
    
    void saveInvoiceWithRejection(SupplierInDTO supplierIn, String rejectionReason);
    
    SupplierInvoiceDTO updateInvoice(Long id, SupplierInvoiceDTO invoiceDTO);
    
    SupplierInvoiceDTO updateInvoiceStatus(Long id, SupplierTransactionStatus status);
    
    void deleteInvoice(Long id);

    /**
     * Get invoices filtered by status and keyword, restricted to a list of allowed statuses
     * @param page Page number (0-based)
     * @param size Items per page
     * @param keyword Optional search term
     * @param status Optional status filter (must be in allowedStatuses)
     * @param allowedStatuses List of statuses to include in results
     * @return Paginated invoice DTOs
     */
    Page<SupplierInvoiceDTO> getFilteredInvoices(int page, int size, String keyword, String status, 
                                        List<SupplierTransactionStatus> allowedStatuses);
    
    /**
     * Save test invoices for debugging purposes
     * @param invoices List of test invoices to save
     */
    void saveTestInvoices(List<SupplierInvoiceEntity> invoices);
} 