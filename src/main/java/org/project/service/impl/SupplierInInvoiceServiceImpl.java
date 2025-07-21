package org.project.service.impl;

import org.project.entity.InventoryManagerEntity;
import org.project.entity.SupplierInvoiceEntity;
import org.project.entity.SupplierTransactionInvoiceMappingEntity;
import org.project.entity.SupplierTransactionInvoiceMappingEntityId;
import org.project.entity.SupplierTransactionsEntity;
import org.project.enums.SupplierTransactionStatus;
import org.project.enums.SupplierTransactionType;
import org.project.model.dto.SupplierInDTO;
import org.project.model.dto.SupplierInvoiceDTO;
import org.project.repository.InventoryManagerRepository;
import org.project.repository.SupplierInvoiceRepository;
import org.project.repository.SupplierTransactionInvoiceMappingRepository;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SupplierInInvoiceServiceImpl implements SupplierInInvoiceService {

    @Autowired
    private SupplierInvoiceRepository supplierInvoiceRepository;
    
    @Autowired
    private SupplierTransactionRepository supplierTransactionRepository;
    
    @Autowired
    private SupplierTransactionInvoiceMappingRepository mappingRepository;
    
    @Autowired
    private InventoryManagerRepository inventoryManagerRepository;

    @Override
    public List<SupplierInvoiceDTO> getAllInvoices() {
        List<SupplierInvoiceEntity> invoices = supplierInvoiceRepository.findByTransactionType(SupplierTransactionType.STOCK_IN);
        return invoices.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<SupplierInvoiceDTO> getAllInvoices(int page, int size, String keyword, String status) {
        Pageable pageable = PageRequest.of(page, size);
        
        Page<SupplierInvoiceEntity> invoicesPage;
        
        if (keyword != null && !keyword.isEmpty() && status != null && !status.isEmpty()) {
            // Tìm kiếm theo cả keyword và status
            SupplierTransactionStatus statusEnum = SupplierTransactionStatus.valueOf(status);
            invoicesPage = supplierInvoiceRepository.findByTransactionTypeAndStatusAndInvoiceNumberContaining(
                    SupplierTransactionType.STOCK_IN, statusEnum, keyword, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            // Chỉ tìm kiếm theo keyword
            invoicesPage = supplierInvoiceRepository.findByTransactionTypeAndInvoiceNumberContaining(
                    SupplierTransactionType.STOCK_IN, keyword, pageable);
        } else if (status != null && !status.isEmpty()) {
            // Chỉ tìm kiếm theo status
            SupplierTransactionStatus statusEnum = SupplierTransactionStatus.valueOf(status);
            invoicesPage = supplierInvoiceRepository.findByTransactionTypeAndStatus(
                    SupplierTransactionType.STOCK_IN, statusEnum, pageable);
        } else {
            // Không có điều kiện tìm kiếm
            invoicesPage = supplierInvoiceRepository.findByTransactionType(
                    SupplierTransactionType.STOCK_IN, pageable);
        }
        
        List<SupplierInvoiceDTO> dtoList = invoicesPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PageImpl<>(dtoList, pageable, invoicesPage.getTotalElements());
    }

    @Override
    public SupplierInvoiceDTO getInvoiceById(Long id) {
        Optional<SupplierInvoiceEntity> invoiceOpt = supplierInvoiceRepository.findById(id);
        return invoiceOpt.filter(i -> i.getTransactionType() == SupplierTransactionType.STOCK_IN)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Override
    @Transactional
    public SupplierInvoiceDTO saveInvoice(SupplierInDTO supplierInDTO) {
        // Lấy transaction từ database
        SupplierTransactionsEntity transaction = supplierTransactionRepository.findById(supplierInDTO.getId())
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + supplierInDTO.getId()));
        
        // Tạo invoice mới
        SupplierInvoiceEntity invoice = new SupplierInvoiceEntity();
        invoice.setInvoiceNumber(supplierInDTO.getInvoiceNumber());
        invoice.setTransactionType(SupplierTransactionType.STOCK_IN);
        invoice.setInvoiceDate(Timestamp.from(Instant.now()));
        invoice.setTotalAmount(supplierInDTO.getTotalAmount());
        invoice.setTaxAmount(supplierInDTO.getTaxAmount());
        invoice.setShippingCost(supplierInDTO.getShippingCost());
        
        // Tính tổng giá trị
        BigDecimal grandTotal = supplierInDTO.getTotalAmount();
        if (supplierInDTO.getTaxAmount() != null) {
            grandTotal = grandTotal.add(supplierInDTO.getTaxAmount());
        }
        if (supplierInDTO.getShippingCost() != null) {
            grandTotal = grandTotal.add(supplierInDTO.getShippingCost());
        }
        invoice.setGrandTotal(grandTotal);
        
        invoice.setStatus(SupplierTransactionStatus.COMPLETED);
        
        // Set người tạo hóa đơn
        InventoryManagerEntity createdBy = inventoryManagerRepository.findById(supplierInDTO.getInventoryManagerId())
                .orElseThrow(() -> new RuntimeException("Inventory Manager not found with id: " + supplierInDTO.getInventoryManagerId()));
        invoice.setCreatedBy(createdBy);
        
        invoice.setNotes(supplierInDTO.getNotes());
        invoice.setPaymentMethod(supplierInDTO.getPaymentMethod());
        invoice.setDueDate(supplierInDTO.getDueDate());
        invoice.setPaymentDate(supplierInDTO.getPaymentDate());
        
        // Lưu invoice
        SupplierInvoiceEntity savedInvoice = supplierInvoiceRepository.save(invoice);
        
        // Tạo mapping giữa transaction và invoice
        SupplierTransactionInvoiceMappingEntity mapping = new SupplierTransactionInvoiceMappingEntity();
        
        // Tạo composite key
        SupplierTransactionInvoiceMappingEntityId mappingId = new SupplierTransactionInvoiceMappingEntityId();
        mappingId.setSupplierTransactionId(transaction.getId());
        mappingId.setSupplierInvoiceId(savedInvoice.getId());
        mapping.setId(mappingId);
        
        // Set references
        mapping.setSupplierTransactionEntity(transaction);
        mapping.setSupplierInvoiceEntity(savedInvoice);
        
        // Set allocated amount (full amount)
        mapping.setAllocatedAmount(transaction.getTotalAmount());
        
        // Lưu mapping
        mappingRepository.save(mapping);
        
        // Cập nhật trạng thái transaction
        transaction.setStatus(SupplierTransactionStatus.COMPLETED);
        supplierTransactionRepository.save(transaction);
        
        return convertToDTO(savedInvoice);
    }

    @Override
    @Transactional
    public SupplierInvoiceDTO updateInvoice(Long id, SupplierInvoiceDTO invoiceDTO) {
        Optional<SupplierInvoiceEntity> invoiceOpt = supplierInvoiceRepository.findById(id);
        if (invoiceOpt.isEmpty() || invoiceOpt.get().getTransactionType() != SupplierTransactionType.STOCK_IN) {
            return null;
        }
        
        SupplierInvoiceEntity invoice = invoiceOpt.get();
        
        // Update fields
        invoice.setInvoiceNumber(invoiceDTO.getInvoiceNumber());
        invoice.setInvoiceDate(invoiceDTO.getInvoiceDate());
        invoice.setTotalAmount(invoiceDTO.getTotalAmount());
        invoice.setTaxAmount(invoiceDTO.getTaxAmount());
        invoice.setShippingCost(invoiceDTO.getShippingCost());
        invoice.setGrandTotal(invoiceDTO.getGrandTotal());
        invoice.setStatus(invoiceDTO.getStatus());
        invoice.setNotes(invoiceDTO.getNotes());
        invoice.setPaymentMethod(invoiceDTO.getPaymentMethod());
        invoice.setDueDate(invoiceDTO.getDueDate());
        invoice.setPaymentDate(invoiceDTO.getPaymentDate());
        
        // Save updated invoice
        SupplierInvoiceEntity updatedInvoice = supplierInvoiceRepository.save(invoice);
        
        return convertToDTO(updatedInvoice);
    }

    @Override
    @Transactional
    public void deleteInvoice(Long id) {
        Optional<SupplierInvoiceEntity> invoiceOpt = supplierInvoiceRepository.findById(id);
        if (invoiceOpt.isPresent() && invoiceOpt.get().getTransactionType() == SupplierTransactionType.STOCK_IN) {
            supplierInvoiceRepository.deleteById(id);
        }
    }
    
    @Override
    @Transactional
    public SupplierInvoiceDTO updateInvoiceStatus(Long id, SupplierTransactionStatus status) {
        Optional<SupplierInvoiceEntity> invoiceOpt = supplierInvoiceRepository.findById(id);
        if (invoiceOpt.isEmpty() || invoiceOpt.get().getTransactionType() != SupplierTransactionType.STOCK_IN) {
            return null;
        }
        
        SupplierInvoiceEntity invoice = invoiceOpt.get();
        invoice.setStatus(status);
        
        // Update related transactions if needed
        for (SupplierTransactionInvoiceMappingEntity mapping : invoice.getTransactionInvoiceMappings()) {
            SupplierTransactionsEntity transaction = mapping.getSupplierTransactionEntity();
            transaction.setStatus(status);
            supplierTransactionRepository.save(transaction);
        }
        
        // Save updated invoice
        SupplierInvoiceEntity updatedInvoice = supplierInvoiceRepository.save(invoice);
        
        return convertToDTO(updatedInvoice);
    }
    
    private SupplierInvoiceDTO convertToDTO(SupplierInvoiceEntity entity) {
        SupplierInvoiceDTO dto = new SupplierInvoiceDTO();
        
        dto.setId(entity.getId());
        dto.setInvoiceNumber(entity.getInvoiceNumber());
        dto.setTransactionType(entity.getTransactionType());
        dto.setInvoiceDate(entity.getInvoiceDate());
        dto.setTotalAmount(entity.getTotalAmount());
        dto.setTaxAmount(entity.getTaxAmount());
        dto.setShippingCost(entity.getShippingCost());
        dto.setGrandTotal(entity.getGrandTotal());
        dto.setStatus(entity.getStatus());
        
        if (entity.getCreatedBy() != null) {
            dto.setCreatedById(entity.getCreatedBy().getId());
        }
        
        dto.setNotes(entity.getNotes());
        dto.setPaymentMethod(entity.getPaymentMethod());
        dto.setDueDate(entity.getDueDate());
        dto.setPaymentDate(entity.getPaymentDate());
        
        return dto;
    }
} 