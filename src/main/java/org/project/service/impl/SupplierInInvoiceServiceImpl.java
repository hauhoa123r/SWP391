package org.project.service.impl;

import org.project.entity.InventoryManagerEntity;
import org.project.entity.SupplierTransactionsEntity;
import org.project.enums.SupplierTransactionStatus;
import org.project.enums.SupplierTransactionType;
import org.project.model.dto.SupplierInDTO;
import org.project.model.dto.SupplierInvoiceDTO;
import org.project.model.dto.SupplierTransactionDTO;
import org.project.repository.InventoryManagerRepository;
import org.project.repository.SupplierTransactionRepository;
import org.project.service.SupplierInInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service("supplierInInvoiceService")
public class SupplierInInvoiceServiceImpl implements SupplierInInvoiceService {

    @Autowired
    private SupplierTransactionRepository supplierTransactionRepository;

    @Override
    @Transactional
    public SupplierInvoiceDTO saveInvoice(SupplierInDTO supplierInDTO) {
        SupplierTransactionsEntity invoice = new SupplierTransactionsEntity();
        invoice.setInvoiceNumber(supplierInDTO.getInvoiceNumber());
        invoice.setTransactionType(SupplierTransactionType.STOCK_IN);
        invoice.setTransactionDate(supplierInDTO.getTransactionDate());
        invoice.setTotalAmount(supplierInDTO.getTotalAmount());
        invoice.setStatus(SupplierTransactionStatus.PENDING);
        // TODO: Set createdBy based on current user context
        SupplierTransactionsEntity savedInvoice = supplierTransactionRepository.save(invoice);
        return convertToInvoiceDTO(savedInvoice);
    }

    @Override
    @Transactional
    public void saveInvoiceWithRejection(SupplierInDTO supplierIn, String rejectionReason) {
        // Tạo hóa đơn mới
        SupplierTransactionsEntity invoice = new SupplierTransactionsEntity();

        // Thiết lập thông tin cơ bản
        invoice.setInvoiceNumber(supplierIn.getInvoiceNumber());
        invoice.setTransactionType(SupplierTransactionType.STOCK_IN);
        invoice.setTransactionDate(Timestamp.from(Instant.now()));
        invoice.setTotalAmount(supplierIn.getTotalAmount());
        invoice.setTaxAmount(supplierIn.getTaxAmount());
        invoice.setShippingCost(supplierIn.getShippingCost());
        invoice.setStatus(SupplierTransactionStatus.REJECTED);
        // Set additional fields if necessary
        invoice.setRejectionReason(rejectionReason); // Uncomment if field exists

        // Lấy supplier entity từ transaction
        SupplierTransactionsEntity transaction = supplierTransactionRepository.findById(supplierIn.getId())
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        invoice.setSupplierEntity(transaction.getSupplierEntity());

        // Lưu hóa đơn
        SupplierTransactionsEntity savedInvoice = supplierTransactionRepository.save(invoice);
    }



    @Override
    public Page<SupplierInvoiceDTO> getAllInvoices(Pageable pageable) {
        Page<SupplierTransactionsEntity> invoicePage = supplierTransactionRepository.findByTransactionType(SupplierTransactionType.STOCK_IN, pageable);
        return invoicePage.map(this::convertToInvoiceDTO);
    }

    @Override
    public Page<SupplierInvoiceDTO> getFilteredInvoices(Pageable pageable, String keyword, String status,
            String startDateStr, String endDateStr) {
        SupplierTransactionStatus transactionStatus = status != null && !status.isEmpty() ? SupplierTransactionStatus.valueOf(status) : null;
        Timestamp start = startDateStr != null && !startDateStr.isEmpty() ? Timestamp.valueOf(startDateStr + " 00:00:00") : null;
        Timestamp end = endDateStr != null && !endDateStr.isEmpty() ? Timestamp.valueOf(endDateStr + " 23:59:59") : null;

        if (keyword != null && !keyword.isEmpty()) {
            return supplierTransactionRepository.findByTransactionTypeAndInvoiceNumberContainingIgnoreCase(SupplierTransactionType.STOCK_IN, keyword, pageable)
                    .map(this::convertToInvoiceDTO);
        } else if (transactionStatus != null || start != null || end != null) {
            return supplierTransactionRepository.findByTransactionTypeAndStatusAndTransactionDateBetween(
                    SupplierTransactionType.STOCK_IN, transactionStatus, start, end, pageable)
                    .map(this::convertToInvoiceDTO);
        } else {
            return getAllInvoices(pageable);
        }
    }

    @Override
    public Page<SupplierInvoiceDTO> getFilteredSupplierInsForStockIn(Pageable pageable, String keyword, String startDateStr, String endDateStr, List<SupplierTransactionStatus> statusList) {
        Timestamp start = startDateStr != null && !startDateStr.isEmpty() ? Timestamp.valueOf(startDateStr + " 00:00:00") : null;
        Timestamp end = endDateStr != null && !endDateStr.isEmpty() ? Timestamp.valueOf(endDateStr + " 23:59:59") : null;

        if (keyword != null && !keyword.isEmpty()) {
            return supplierTransactionRepository.findByTransactionTypeAndInvoiceNumberContainingIgnoreCase(SupplierTransactionType.STOCK_IN, keyword, pageable)
                    .map(this::convertToInvoiceDTO);
        } else if (statusList != null && !statusList.isEmpty()) {
            List<SupplierTransactionStatus> allowedStatuses = Arrays.asList(
                    SupplierTransactionStatus.WAITING_FOR_DELIVERY,
                    SupplierTransactionStatus.INSPECTED,
                    SupplierTransactionStatus.RECEIVED
            );
            
            System.out.println("SupplierInService querying with type: " + SupplierTransactionType.STOCK_IN.toString());
            System.out.println("SupplierInService allowed statuses: " + allowedStatuses.toString());
            
            Page<SupplierTransactionsEntity> supplierInsPage = supplierTransactionRepository.findByTransactionTypeAndStatusIn(
                    SupplierTransactionType.STOCK_IN,
                    allowedStatuses,
                    pageable);
            
            // Add null check to prevent NullPointerException
            if (supplierInsPage != null) {
                System.out.println("SupplierInService result count: " + supplierInsPage.getTotalElements());
            } else {
                System.out.println("SupplierInService result count: null page result");
            }
            return supplierInsPage.map(this::convertToInvoiceDTO);
        } else if (start != null && end != null) {
            // Temporarily get all results without pagination due to method signature limitation
            List<SupplierTransactionsEntity> results = supplierTransactionRepository.findByTransactionTypeAndTransactionDateBetween(SupplierTransactionType.STOCK_IN, start, end);
            // Convert to Page manually
            int startIndex = (int) pageable.getOffset();
            int endIndex = Math.min(startIndex + pageable.getPageSize(), results.size());
            List<SupplierTransactionsEntity> pagedResults = results.subList(startIndex, endIndex);
            return new PageImpl<>(pagedResults.stream().map(this::convertToInvoiceDTO).collect(Collectors.toList()), pageable, results.size());
        } else {
            return getAllInvoices(pageable);
        }
    }


    @Override
    public void rejectInvoice(Long id, String rejectionReason) {
        Optional<SupplierTransactionsEntity> invoiceOpt = supplierTransactionRepository.findById(id);
        if (invoiceOpt.isPresent()) {
            SupplierTransactionsEntity invoice = invoiceOpt.get();
            invoice.setStatus(SupplierTransactionStatus.REJECTED);
            invoice.setRejectionReason(rejectionReason);
            supplierTransactionRepository.save(invoice);
        } else {
            throw new RuntimeException("Invoice not found with id: " + id);
        }
    }

    private SupplierInvoiceDTO convertToInvoiceDTO(SupplierTransactionsEntity entity) {
        SupplierInvoiceDTO dto = new SupplierInvoiceDTO();
        dto.setId(entity.getId());
        dto.setInvoiceNumber(entity.getInvoiceNumber());
        dto.setTransactionDate(entity.getTransactionDate());
        dto.setTotalAmount(entity.getTotalAmount());
        dto.setStatus(entity.getStatus());
        dto.setTransactionType(entity.getTransactionType());
        // TODO: Set createdByName from entity.getCreatedBy()
        // dto.setCreatedByName(entity.getCreatedBy() != null ? entity.getCreatedBy().getName() : "Unknown");
        return dto;
    }
}