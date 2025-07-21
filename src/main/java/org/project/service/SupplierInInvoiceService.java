package org.project.service;

import org.project.enums.SupplierTransactionStatus;
import org.project.model.dto.SupplierInDTO;
import org.project.model.dto.SupplierInvoiceDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SupplierInInvoiceService {
    List<SupplierInvoiceDTO> getAllInvoices();

    Page<SupplierInvoiceDTO> getAllInvoices(int page, int size, String keyword, String status);

    SupplierInvoiceDTO getInvoiceById(Long id);

    SupplierInvoiceDTO saveInvoice(SupplierInDTO supplierInDTO);

    SupplierInvoiceDTO updateInvoice(Long id, SupplierInvoiceDTO invoiceDTO);

    SupplierInvoiceDTO updateInvoiceStatus(Long id, SupplierTransactionStatus status);

    void deleteInvoice(Long id);
}