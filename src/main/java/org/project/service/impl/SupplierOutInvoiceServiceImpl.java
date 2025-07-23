package org.project.service.impl;

import org.project.entity.InventoryManagerEntity;
import org.project.entity.SupplierInvoiceEntity;
import org.project.entity.SupplierTransactionInvoiceMappingEntity;
import org.project.entity.SupplierTransactionInvoiceMappingEntityId;
import org.project.entity.SupplierTransactionsEntity;
import org.project.enums.SupplierTransactionStatus;
import org.project.enums.SupplierTransactionType;
import org.project.model.dto.SupplierOutDTO;
import org.project.model.dto.SupplierInvoiceDTO;
import org.project.repository.InventoryManagerRepository;
import org.project.repository.SupplierInvoiceRepository;
import org.project.repository.SupplierTransactionInvoiceMappingRepository;
import org.project.repository.SupplierTransactionRepository;
import org.project.service.SupplierOutInvoiceService;
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
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SupplierOutInvoiceServiceImpl implements SupplierOutInvoiceService {

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
        List<SupplierInvoiceEntity> invoices = supplierInvoiceRepository.findByTransactionType(SupplierTransactionType.STOCK_OUT);
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
                    SupplierTransactionType.STOCK_OUT, statusEnum, keyword, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            // Chỉ tìm kiếm theo keyword
            invoicesPage = supplierInvoiceRepository.findByTransactionTypeAndInvoiceNumberContaining(
                    SupplierTransactionType.STOCK_OUT, keyword, pageable);
        } else if (status != null && !status.isEmpty()) {
            // Chỉ tìm kiếm theo status
            SupplierTransactionStatus statusEnum = SupplierTransactionStatus.valueOf(status);
            invoicesPage = supplierInvoiceRepository.findByTransactionTypeAndStatus(
                    SupplierTransactionType.STOCK_OUT, statusEnum, pageable);
        } else {
            // Không có điều kiện tìm kiếm
            invoicesPage = supplierInvoiceRepository.findByTransactionType(
                    SupplierTransactionType.STOCK_OUT, pageable);
        }
        
        List<SupplierInvoiceDTO> dtoList = invoicesPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PageImpl<>(dtoList, pageable, invoicesPage.getTotalElements());
    }

    @Override
    public SupplierInvoiceDTO getInvoiceById(Long id) {
        Optional<SupplierInvoiceEntity> invoiceOpt = supplierInvoiceRepository.findById(id);
        return invoiceOpt.filter(i -> i.getTransactionType() == SupplierTransactionType.STOCK_OUT)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Override
    @Transactional
    public SupplierInvoiceDTO saveInvoice(SupplierOutDTO supplierOutDTO) {
        // Lấy transaction từ database
        SupplierTransactionsEntity transaction = supplierTransactionRepository.findById(supplierOutDTO.getId())
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + supplierOutDTO.getId()));
        
        // Tạo invoice mới
        SupplierInvoiceEntity invoice = new SupplierInvoiceEntity();
        invoice.setInvoiceNumber(supplierOutDTO.getInvoiceNumber());
        invoice.setTransactionType(SupplierTransactionType.STOCK_OUT);
        invoice.setInvoiceDate(Timestamp.from(Instant.now()));
        invoice.setTotalAmount(supplierOutDTO.getTotalAmount());
        invoice.setTaxAmount(supplierOutDTO.getTaxAmount());
        invoice.setShippingCost(supplierOutDTO.getShippingCost());
        
        // Tính tổng giá trị
        BigDecimal grandTotal = supplierOutDTO.getTotalAmount();
        if (supplierOutDTO.getTaxAmount() != null) {
            grandTotal = grandTotal.add(supplierOutDTO.getTaxAmount());
        }
        if (supplierOutDTO.getShippingCost() != null) {
            grandTotal = grandTotal.add(supplierOutDTO.getShippingCost());
        }
        invoice.setGrandTotal(grandTotal);
        
        invoice.setStatus(SupplierTransactionStatus.COMPLETED);
        
        // Set người tạo hóa đơn
        InventoryManagerEntity createdBy = inventoryManagerRepository.findById(supplierOutDTO.getInventoryManagerId())
                .orElseThrow(() -> new RuntimeException("Inventory Manager not found with id: " + supplierOutDTO.getInventoryManagerId()));
        invoice.setCreatedBy(createdBy);
        
        invoice.setNotes(supplierOutDTO.getNotes());
        invoice.setPaymentMethod(supplierOutDTO.getPaymentMethod());
        invoice.setDueDate(supplierOutDTO.getDueDate());
        invoice.setPaymentDate(supplierOutDTO.getPaymentDate());
        
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
        if (invoiceOpt.isEmpty() || invoiceOpt.get().getTransactionType() != SupplierTransactionType.STOCK_OUT) {
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
        if (invoiceOpt.isPresent() && invoiceOpt.get().getTransactionType() == SupplierTransactionType.STOCK_OUT) {
            supplierInvoiceRepository.deleteById(id);
        }
    }
    
    @Override
    @Transactional
    public SupplierInvoiceDTO updateInvoiceStatus(Long id, SupplierTransactionStatus status) {
        Optional<SupplierInvoiceEntity> invoiceOpt = supplierInvoiceRepository.findById(id);
        if (invoiceOpt.isEmpty() || invoiceOpt.get().getTransactionType() != SupplierTransactionType.STOCK_OUT) {
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
        
        // Map additional fields for StockOut
        // Lấy thông tin recipient và stockOutReason từ transaction
        Set<SupplierTransactionInvoiceMappingEntity> mappings = entity.getTransactionInvoiceMappings();
        if (mappings != null && !mappings.isEmpty()) {
            // Thường một invoice chỉ liên kết với một transaction, lấy transaction đầu tiên
            SupplierTransactionsEntity transaction = mappings.iterator().next().getSupplierTransactionEntity();
            if (transaction != null) {
                // Lấy recipient và stockOutReason từ transaction
                dto.setRecipient(transaction.getRecipient());
                dto.setStockOutReason(transaction.getStockOutReason());
            }
        }
        
        return dto;
    }

    @Override
    public Page<SupplierInvoiceDTO> getAllInvoicesWithDateRange(Pageable pageable, String keyword, String status,
            Timestamp startDate, Timestamp endDate) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllInvoicesWithDateRange'");
    }

    @Override
    public Page<SupplierInvoiceDTO> getFilteredInvoices(int page, int size, String keyword, String status, 
                                        List<SupplierTransactionStatus> allowedStatuses) {
        Pageable pageable = PageRequest.of(page, size);
        
        // Xử lý status parameter
        SupplierTransactionStatus statusFilter = null;
        if (status != null && !status.isEmpty()) {
            try {
                SupplierTransactionStatus tempStatus = SupplierTransactionStatus.valueOf(status);
                if (allowedStatuses.contains(tempStatus)) {
                    statusFilter = tempStatus;
                }
            } catch (IllegalArgumentException e) {
                // Invalid status, keep it null
            }
        }
        
        try {
            // Lấy toàn bộ danh sách hóa đơn xuất kho
            List<SupplierInvoiceEntity> allInvoices = supplierInvoiceRepository.findByTransactionType(
                    SupplierTransactionType.STOCK_OUT);
            
            // Log tổng số hóa đơn xuất kho
            System.out.println("Tổng số hóa đơn xuất kho: " + allInvoices.size());
            
            // Lọc theo trạng thái được phép (allowedStatuses)
            final SupplierTransactionStatus statusToFilter = statusFilter;
            List<SupplierInvoiceEntity> filteredInvoices = allInvoices.stream()
                    .filter(invoice -> invoice.getStatus() != null && allowedStatuses.contains(invoice.getStatus()))
                    .collect(Collectors.toList());
            
            // Log số hóa đơn sau khi lọc theo trạng thái
            System.out.println("Số hóa đơn xuất kho sau khi lọc theo trạng thái: " + filteredInvoices.size());
            
            // Nếu có status cụ thể, tiếp tục lọc
            if (statusToFilter != null) {
                filteredInvoices = filteredInvoices.stream()
                        .filter(invoice -> statusToFilter.equals(invoice.getStatus()))
                        .collect(Collectors.toList());
                System.out.println("Số hóa đơn xuất kho sau khi lọc theo status cụ thể " + statusToFilter + ": " 
                        + filteredInvoices.size());
            }
            
            // Nếu có từ khóa tìm kiếm, tiếp tục lọc
            if (keyword != null && !keyword.isEmpty()) {
                final String lowerCaseKeyword = keyword.toLowerCase();
                filteredInvoices = filteredInvoices.stream()
                        .filter(invoice -> invoice.getInvoiceNumber() != null && 
                                invoice.getInvoiceNumber().toLowerCase().contains(lowerCaseKeyword))
                        .collect(Collectors.toList());
                System.out.println("Số hóa đơn xuất kho sau khi lọc theo từ khóa: " + filteredInvoices.size());
            }
            
            // Tạo trang kết quả
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), filteredInvoices.size());
            
            // Phân trang
            List<SupplierInvoiceEntity> pageContent = start < end ? 
                    filteredInvoices.subList(start, end) : List.of();
            
            // Chuyển đổi sang DTOs
            List<SupplierInvoiceDTO> dtoList = pageContent.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            return new PageImpl<>(dtoList, pageable, filteredInvoices.size());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Lỗi khi lọc hóa đơn xuất kho: " + e.getMessage());
            return new PageImpl<>(List.of(), pageable, 0);
        }
    }

    @Override
    public void saveTestInvoices(List<SupplierInvoiceEntity> invoices) {
        supplierInvoiceRepository.saveAll(invoices);
    }
} 