package org.project.service;

import org.project.entity.SupplierTransactionsEntity;
import org.project.enums.SupplierTransactionStatus;
import org.project.model.dto.SupplierInvoiceDTO;
import org.project.model.dto.SupplierOutDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.util.List;

public interface SupplierOutInvoiceService {

    Page<SupplierInvoiceDTO> getAllInvoices(Pageable pageable);



    SupplierInvoiceDTO saveInvoice(SupplierOutDTO supplierOutDTO);

    void createInvoiceFromSupplierOut(SupplierOutDTO supplierOut);
}