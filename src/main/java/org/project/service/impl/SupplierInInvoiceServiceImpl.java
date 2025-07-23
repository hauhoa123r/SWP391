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
import java.util.Set;
import java.util.stream.Collectors;

@Service("supplierInInvoiceService")
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
    public void saveInvoiceWithRejection(SupplierInDTO supplierIn, String rejectionReason) {
        // Tạo hóa đơn mới
        SupplierInvoiceEntity invoice = new SupplierInvoiceEntity();
        
        // Thiết lập thông tin cơ bản
        invoice.setInvoiceNumber(supplierIn.getInvoiceNumber());
        invoice.setInvoiceDate(Timestamp.from(Instant.now()));
        
        // Lấy supplier entity từ transaction
        SupplierTransactionsEntity transaction = supplierTransactionRepository.findById(supplierIn.getId())
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        invoice.setTransactionType(SupplierTransactionType.STOCK_IN);
        invoice.setStatus(SupplierTransactionStatus.REJECTED);
        invoice.setTotalAmount(supplierIn.getTotalAmount());
        invoice.setNotes(supplierIn.getNotes());
        
        // Thêm supplier entity
        invoice.setSupplierEntity(transaction.getSupplierEntity());
        
        // Thêm lý do từ chối
        invoice.setRejectionReason(rejectionReason);
        
        // Lưu hóa đơn
        SupplierInvoiceEntity savedInvoice = supplierInvoiceRepository.save(invoice);
        
        // Tạo mapping giữa giao dịch và hóa đơn
        SupplierTransactionInvoiceMappingEntity mapping = new SupplierTransactionInvoiceMappingEntity();
        SupplierTransactionInvoiceMappingEntityId mappingId = new SupplierTransactionInvoiceMappingEntityId();
        mappingId.setSupplierTransactionId(supplierIn.getId());
        mappingId.setSupplierInvoiceId(savedInvoice.getId());
        mapping.setId(mappingId);
        
        mapping.setSupplierTransactionEntity(transaction);
        mapping.setSupplierInvoiceEntity(savedInvoice);
        
        mappingRepository.save(mapping);
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
        
        // Map additional fields for related transactions
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
        
        // Xử lý status parameter - Chỉ chấp nhận COMPLETED hoặc REJECTED
        final SupplierTransactionStatus statusFilter;
        if (status != null && !status.isEmpty()) {
            if ("COMPLETED".equals(status)) {
                statusFilter = SupplierTransactionStatus.COMPLETED;
            } else if ("REJECTED".equals(status)) {
                statusFilter = SupplierTransactionStatus.REJECTED;
            } else {
                statusFilter = null;
            }
        } else {
            statusFilter = null;
        }
        
        try {
            // Lấy các đơn có trạng thái COMPLETED hoặc REJECTED bằng JPQL để tránh lỗi
            List<SupplierInvoiceEntity> allInvoices = supplierInvoiceRepository
                    .findCompletedOrRejectedInvoicesByType(SupplierTransactionType.STOCK_IN);
            
            // Log tổng số hóa đơn
            System.out.println("Tổng số hóa đơn nhập kho (COMPLETED+REJECTED): " + allInvoices.size());
            
            // Lọc theo status được chọn nếu có
            List<SupplierInvoiceEntity> filteredInvoices = allInvoices;
            
            // Nếu có status cụ thể, lọc theo status đó
            if (statusFilter != null) {
                filteredInvoices = allInvoices.stream()
                        .filter(invoice -> statusFilter.equals(invoice.getStatus()))
                        .collect(Collectors.toList());
                System.out.println("Số hóa đơn sau khi lọc theo status " + statusFilter + ": " + filteredInvoices.size());
            }
            
            // Nếu có từ khóa tìm kiếm, tiếp tục lọc theo keyword
            if (keyword != null && !keyword.isEmpty()) {
                final String lowerCaseKeyword = keyword.toLowerCase();
                filteredInvoices = filteredInvoices.stream()
                        .filter(invoice -> invoice.getInvoiceNumber() != null && 
                                invoice.getInvoiceNumber().toLowerCase().contains(lowerCaseKeyword))
                        .collect(Collectors.toList());
                System.out.println("Số hóa đơn sau khi lọc theo từ khóa: " + filteredInvoices.size());
            }
            
            // Tạo trang kết quả
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), filteredInvoices.size());
            List<SupplierInvoiceEntity> pageContent = start < end ? filteredInvoices.subList(start, end) : List.of();
            
            // Convert sang DTO
            List<SupplierInvoiceDTO> dtoList = pageContent.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            return new PageImpl<>(dtoList, pageable, filteredInvoices.size());
        } catch (Exception e) {
            // Log lỗi
            e.printStackTrace();
            System.out.println("Lỗi khi lọc hóa đơn: " + e.getMessage());
            // Return empty page
            return new PageImpl<>(List.of(), pageable, 0);
        }
    }

    @Override
    public void saveTestInvoices(List<SupplierInvoiceEntity> invoices) {
        supplierInvoiceRepository.saveAll(invoices);
    }
} 