package org.project.service.impl;

import org.project.entity.InventoryManagerEntity;
import org.project.entity.SupplierTransactionsEntity;
import org.project.enums.SupplierTransactionStatus;
import org.project.enums.SupplierTransactionType;
import org.project.model.dto.SupplierInvoiceDTO;
import org.project.model.dto.SupplierOutDTO;
import org.project.repository.InventoryManagerRepository;
import org.project.repository.SupplierTransactionRepository;
import org.project.service.SupplierOutInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("supplierOutInvoiceService")
public class SupplierOutInvoiceServiceImpl implements SupplierOutInvoiceService {

    @Autowired
    private SupplierTransactionRepository supplierTransactionRepository;

    @Autowired
    private InventoryManagerRepository inventoryManagerRepository;

    @Override
    public SupplierInvoiceDTO saveInvoice(SupplierOutDTO supplierOutDTO) {
        SupplierTransactionsEntity invoice = new SupplierTransactionsEntity();
        invoice.setInvoiceNumber(supplierOutDTO.getInvoiceNumber());
        invoice.setTransactionDate(supplierOutDTO.getTransactionDate());
        invoice.setTotalAmount(supplierOutDTO.getTotalAmount());
        invoice.setStatus(supplierOutDTO.getStatus());
        invoice.setTransactionType(SupplierTransactionType.STOCK_OUT);
        // Set createdBy from inventory manager if available
        if (supplierOutDTO.getInventoryManagerId() != null) {
            inventoryManagerRepository.findById(supplierOutDTO.getInventoryManagerId())
                    .ifPresent(invoice::setCreatedBy);
        }
        SupplierTransactionsEntity savedInvoice = supplierTransactionRepository.save(invoice);
        return convertToInvoiceDTO(savedInvoice);
    }

    @Override
    public void createInvoiceFromSupplierOut(SupplierOutDTO supplierOut) {
        if (supplierOut == null) {
            throw new IllegalArgumentException("SupplierOutDTO cannot be null");
        }

        SupplierTransactionsEntity invoice = new SupplierTransactionsEntity();
        invoice.setInvoiceNumber(supplierOut.getInvoiceNumber());
        invoice.setTransactionDate(supplierOut.getTransactionDate() != null ? supplierOut.getTransactionDate() : Timestamp.from(Instant.now()));
        invoice.setTotalAmount(supplierOut.getTotalAmount());
        invoice.setTaxAmount(supplierOut.getTaxAmount());
        invoice.setShippingCost(supplierOut.getShippingCost());
        invoice.setStatus(supplierOut.getStatus() != null ? supplierOut.getStatus() : SupplierTransactionStatus.PREPARING);
        invoice.setTransactionType(SupplierTransactionType.STOCK_OUT);
        invoice.setPaymentMethod(supplierOut.getPaymentMethod());
        invoice.setPaymentDate(supplierOut.getPaymentDate());

        // Set createdBy from inventory manager if available
        if (supplierOut.getInventoryManagerId() != null) {
            inventoryManagerRepository.findById(supplierOut.getInventoryManagerId())
                    .ifPresent(invoice::setCreatedBy);
        }

        supplierTransactionRepository.save(invoice);
    }

    @Override
    public Page<SupplierInvoiceDTO> getAllInvoices(Pageable pageable) {
        Page<SupplierTransactionsEntity> invoicePage = supplierTransactionRepository.findByTransactionType(SupplierTransactionType.STOCK_OUT, pageable);
        return invoicePage.map(this::convertToInvoiceDTO);
    }

    private SupplierInvoiceDTO convertToInvoiceDTO(SupplierTransactionsEntity entity) {
        SupplierInvoiceDTO dto = new SupplierInvoiceDTO();
        dto.setId(entity.getId());
        dto.setInvoiceNumber(entity.getInvoiceNumber());
        dto.setTransactionDate(entity.getTransactionDate());
        dto.setTotalAmount(entity.getTotalAmount());
        dto.setStatus(entity.getStatus());
        dto.setTransactionType(entity.getTransactionType());
        return dto;
    }
}