package org.project.service.impl;

import org.project.entity.InventoryManagerEntity;
import org.project.entity.ProductEntity;
import org.project.entity.SupplierEntity;
import org.project.entity.SupplierTransactionItemEntity;
import org.project.entity.SupplierTransactionItemEntityId;
import org.project.entity.SupplierTransactionsEntity;
import org.project.enums.SupplierTransactionStatus;
import org.project.enums.SupplierTransactionType;
import org.project.model.dto.SupplierOutDTO;
import org.project.model.dto.SupplierRequestItemDTO;
import org.project.repository.InventoryManagerRepository;
import org.project.repository.ProductRepository;
import org.project.repository.SupplierEntityRepository;
import org.project.repository.SupplierTransactionItemRepository;
import org.project.repository.SupplierTransactionRepository;
import org.project.service.SupplierOutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SupplierOutServiceImpl implements SupplierOutService {

    @Autowired
    private SupplierTransactionRepository supplierTransactionRepository;

    @Autowired
    private SupplierEntityRepository supplierEntityRepository;

    @Autowired
    private InventoryManagerRepository inventoryManagerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierTransactionItemRepository supplierTransactionItemRepository;

    @Override
    public List<SupplierOutDTO> getAllSupplierOuts() {
        List<SupplierTransactionsEntity> transactions = supplierTransactionRepository.findByTransactionType(SupplierTransactionType.STOCK_OUT);
        return transactions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<SupplierOutDTO> getAllSupplierOuts(int page, int size, String keyword, String status) {
        Pageable pageable = PageRequest.of(page, size);
        
        // Thực hiện tìm kiếm dựa trên keyword và status
        Page<SupplierTransactionsEntity> transactionsPage;
        
        if (keyword != null && !keyword.isEmpty() && status != null && !status.isEmpty()) {
            // Tìm kiếm theo cả keyword và status
            SupplierTransactionStatus statusEnum = SupplierTransactionStatus.valueOf(status);
            transactionsPage = supplierTransactionRepository.findByTransactionTypeAndStatusAndInvoiceNumberContaining(
                    SupplierTransactionType.STOCK_OUT, statusEnum, keyword, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            // Chỉ tìm kiếm theo keyword
            transactionsPage = supplierTransactionRepository.findByTransactionTypeAndInvoiceNumberContaining(
                    SupplierTransactionType.STOCK_OUT, keyword, pageable);
        } else if (status != null && !status.isEmpty()) {
            // Chỉ tìm kiếm theo status
            SupplierTransactionStatus statusEnum = SupplierTransactionStatus.valueOf(status);
            transactionsPage = supplierTransactionRepository.findByTransactionTypeAndStatus(
                    SupplierTransactionType.STOCK_OUT, statusEnum, pageable);
        } else {
            // Không có điều kiện tìm kiếm
            transactionsPage = supplierTransactionRepository.findByTransactionType(
                    SupplierTransactionType.STOCK_OUT, pageable);
        }
        
        List<SupplierOutDTO> dtoList = transactionsPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PageImpl<>(dtoList, pageable, transactionsPage.getTotalElements());
    }
    
    @Override
    public Page<SupplierOutDTO> getFilteredSupplierOuts(int page, int size, String status, String search, String type) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<SupplierTransactionsEntity> transactionsPage;
        
        // Define allowed statuses for StockOut
        List<SupplierTransactionStatus> allowedStatuses = List.of(
            SupplierTransactionStatus.PREPARE_DELIVERY,  // Chuẩn bị giao hàng
            SupplierTransactionStatus.DELIVERING,       // Đang giao hàng
            SupplierTransactionStatus.DELIVERED,        // Đã giao hàng
            SupplierTransactionStatus.PENDING           // Chờ thanh toán
        );

        // Build filtering logic based on status parameter
        if (status != null && !status.isEmpty()) {
            try {
                SupplierTransactionStatus statusEnum = SupplierTransactionStatus.valueOf(status.toUpperCase());
                
                // Only proceed if the requested status is in the allowed list
                if (allowedStatuses.contains(statusEnum)) {
                    if (search != null && !search.isEmpty()) {
                        // Search with status and search term
                        transactionsPage = supplierTransactionRepository.findByTransactionTypeAndStatusAndSupplierEntityNameContainingIgnoreCase(
                                SupplierTransactionType.STOCK_OUT, statusEnum, search, pageable);
                    } else {
                        // Just by status
                        transactionsPage = supplierTransactionRepository.findByTransactionTypeAndStatus(
                                SupplierTransactionType.STOCK_OUT, statusEnum, pageable);
                    }
                } else {
                    // If status is not allowed, return empty page
                    return new PageImpl<>(List.of(), pageable, 0);
                }
            } catch (IllegalArgumentException e) {
                // Invalid status, return empty page
                return new PageImpl<>(List.of(), pageable, 0);
            }
        } else {
            // No specific status requested - get all with allowed statuses
            if (search != null && !search.isEmpty()) {
                // Search with allowed statuses
                transactionsPage = supplierTransactionRepository.findByTransactionTypeAndStatusInAndSupplierEntityNameContainingIgnoreCase(
                        SupplierTransactionType.STOCK_OUT, 
                        allowedStatuses,
                        search, 
                        pageable);
            } else {
                // Just get all allowed statuses
                transactionsPage = supplierTransactionRepository.findByTransactionTypeAndStatusIn(
                        SupplierTransactionType.STOCK_OUT,
                        allowedStatuses,
                        pageable);
            }
        }
        
        // Convert to DTOs
        List<SupplierOutDTO> dtoList = transactionsPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PageImpl<>(dtoList, pageable, transactionsPage.getTotalElements());
    }

    @Override
    public SupplierOutDTO getSupplierOutById(Long id) {
        Optional<SupplierTransactionsEntity> transactionOpt = supplierTransactionRepository.findById(id);
        return transactionOpt.filter(t -> t.getTransactionType() == SupplierTransactionType.STOCK_OUT)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Override
    @Transactional
    public SupplierOutDTO createSupplierOut(SupplierOutDTO supplierOutDTO) {
        SupplierTransactionsEntity transaction = new SupplierTransactionsEntity();
        
        // Set basic information
        transaction.setTransactionType(SupplierTransactionType.STOCK_OUT);
        transaction.setStatus(supplierOutDTO.getStatus() != null ? supplierOutDTO.getStatus() : SupplierTransactionStatus.PENDING);
        transaction.setTransactionDate(supplierOutDTO.getTransactionDate() != null ? supplierOutDTO.getTransactionDate() : Timestamp.from(Instant.now()));
        transaction.setTotalAmount(supplierOutDTO.getTotalAmount());
        transaction.setNotes(supplierOutDTO.getNotes());
        transaction.setExpectedDeliveryDate(supplierOutDTO.getExpectedDeliveryDate());
        transaction.setInvoiceNumber(supplierOutDTO.getInvoiceNumber());
        transaction.setTaxAmount(supplierOutDTO.getTaxAmount());
        transaction.setShippingCost(supplierOutDTO.getShippingCost());
        transaction.setPaymentMethod(supplierOutDTO.getPaymentMethod());
        transaction.setDueDate(supplierOutDTO.getDueDate());
        transaction.setPaymentDate(supplierOutDTO.getPaymentDate());
        
        // Set new fields
        transaction.setRecipient(supplierOutDTO.getRecipient() != null ? supplierOutDTO.getRecipient() : "Hospital");
        transaction.setStockOutReason(supplierOutDTO.getStockOutReason());
        
        // Set supplier
        SupplierEntity supplier = supplierEntityRepository.findById(supplierOutDTO.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + supplierOutDTO.getSupplierId()));
        transaction.setSupplierEntity(supplier);
        
        // Set inventory manager
        InventoryManagerEntity manager = inventoryManagerRepository.findById(supplierOutDTO.getInventoryManagerId())
                .orElseThrow(() -> new RuntimeException("Inventory Manager not found with id: " + supplierOutDTO.getInventoryManagerId()));
        transaction.setInventoryManagerEntity(manager);
        
        // Save transaction first to get ID
        SupplierTransactionsEntity savedTransaction = supplierTransactionRepository.save(transaction);
        
        // Process items if available
        if (supplierOutDTO.getItems() != null && !supplierOutDTO.getItems().isEmpty()) {
            List<SupplierTransactionItemEntity> itemEntities = new ArrayList<>();
            
            for (SupplierRequestItemDTO itemDTO : supplierOutDTO.getItems()) {
                ProductEntity product = productRepository.findById(itemDTO.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found with id: " + itemDTO.getProductId()));
                
                SupplierTransactionItemEntity item = new SupplierTransactionItemEntity();
                item.setId(new SupplierTransactionItemEntityId(savedTransaction.getId(), product.getId()));
                item.setSupplierTransactionEntity(savedTransaction);
                item.setProductEntity(product);
                item.setQuantity(itemDTO.getQuantity());
                item.setUnitPrice(itemDTO.getUnitPrice());
                item.setManufactureDate(itemDTO.getManufactureDate());
                item.setExpirationDate(itemDTO.getExpirationDate());
                item.setBatchNumber(itemDTO.getBatchNumber());
                item.setStorageLocation(itemDTO.getStorageLocation());
                item.setNotes(itemDTO.getNotes());
                
                itemEntities.add(item);
            }
            
            supplierTransactionItemRepository.saveAll(itemEntities);
        }
        
        return convertToDTO(savedTransaction);
    }
    
    @Override
    @Transactional
    public SupplierOutDTO updateSupplierOut(Long id, SupplierOutDTO supplierOutDTO) {
        Optional<SupplierTransactionsEntity> transactionOpt = supplierTransactionRepository.findById(id);
        if (transactionOpt.isPresent()) {
            SupplierTransactionsEntity transaction = transactionOpt.get();
            
            // Update basic information
            if (supplierOutDTO.getStatus() != null) {
                transaction.setStatus(supplierOutDTO.getStatus());
            }
            if (supplierOutDTO.getTransactionDate() != null) {
                transaction.setTransactionDate(supplierOutDTO.getTransactionDate());
            }
            if (supplierOutDTO.getTotalAmount() != null) {
                transaction.setTotalAmount(supplierOutDTO.getTotalAmount());
            }
            transaction.setNotes(supplierOutDTO.getNotes());
            transaction.setExpectedDeliveryDate(supplierOutDTO.getExpectedDeliveryDate());
            transaction.setInvoiceNumber(supplierOutDTO.getInvoiceNumber());
            transaction.setTaxAmount(supplierOutDTO.getTaxAmount());
            transaction.setShippingCost(supplierOutDTO.getShippingCost());
            transaction.setPaymentMethod(supplierOutDTO.getPaymentMethod());
            transaction.setDueDate(supplierOutDTO.getDueDate());
            transaction.setPaymentDate(supplierOutDTO.getPaymentDate());
            
            // Update new fields
            if (supplierOutDTO.getRecipient() != null) {
                transaction.setRecipient(supplierOutDTO.getRecipient());
            }
            transaction.setStockOutReason(supplierOutDTO.getStockOutReason());
            
            // Update supplier if provided
            if (supplierOutDTO.getSupplierId() != null) {
                SupplierEntity supplier = supplierEntityRepository.findById(supplierOutDTO.getSupplierId())
                        .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + supplierOutDTO.getSupplierId()));
                transaction.setSupplierEntity(supplier);
            }
            
            SupplierTransactionsEntity updatedTransaction = supplierTransactionRepository.save(transaction);
            return convertToDTO(updatedTransaction);
        } else {
            throw new RuntimeException("Supplier Out with ID " + id + " not found");
        }
    }

    @Override
    @Transactional
    public void updateSupplierOutStatus(Long id, String status) {
        SupplierTransactionsEntity transaction = supplierTransactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier Out not found with id: " + id));
        
        if (transaction.getTransactionType() != SupplierTransactionType.STOCK_OUT) {
            throw new RuntimeException("Transaction is not a Stock Out");
        }
        
        // Validate current status against new status
        SupplierTransactionStatus currentStatus = transaction.getStatus();
        SupplierTransactionStatus newStatus = SupplierTransactionStatus.valueOf(status);
        
        transaction.setStatus(newStatus);
        
        // If status is COMPLETED, set approvedDate
        if (SupplierTransactionStatus.COMPLETED.name().equals(status)) {
            transaction.setApprovedDate(Timestamp.from(Instant.now()));
        }
        
        supplierTransactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public void deleteSupplierOut(Long id) {
        Optional<SupplierTransactionsEntity> transaction = supplierTransactionRepository.findById(id);
        if (transaction.isPresent()) {
            supplierTransactionRepository.deleteById(id);
        } else {
            throw new RuntimeException("Supplier Out with ID " + id + " not found");
        }
    }
    
    @Override
    @Transactional
    public void addRejectionReason(Long id, String reason) {
        Optional<SupplierTransactionsEntity> transactionOpt = supplierTransactionRepository.findById(id);
        if (transactionOpt.isPresent()) {
            SupplierTransactionsEntity transaction = transactionOpt.get();
            transaction.setNotes(transaction.getNotes() != null ? 
                              transaction.getNotes() + " | Từ chối: " + reason : 
                              "Từ chối: " + reason);
            supplierTransactionRepository.save(transaction);
        } else {
            throw new RuntimeException("Supplier Out with ID " + id + " not found");
        }
    }
    
    private SupplierOutDTO convertToDTO(SupplierTransactionsEntity entity) {
        SupplierOutDTO dto = new SupplierOutDTO();
        
        dto.setId(entity.getId());
        dto.setSupplierId(entity.getSupplierEntity().getId());
        dto.setSupplierName(entity.getSupplierEntity().getName());
        dto.setSupplierContact(entity.getSupplierEntity().getPhoneNumber());
        dto.setInventoryManagerId(entity.getInventoryManagerEntity().getId());
        
        // Get staff name from inventory manager's staff entity
        if (entity.getInventoryManagerEntity().getStaffEntity() != null) {
            dto.setInventoryManagerName(entity.getInventoryManagerEntity().getStaffEntity().getFullName());
        }
        
        dto.setTotalAmount(entity.getTotalAmount());
        dto.setTransactionDate(entity.getTransactionDate());
        dto.setStatus(entity.getStatus());
        dto.setApprovedDate(entity.getApprovedDate());
        dto.setNotes(entity.getNotes());
        dto.setExpectedDeliveryDate(entity.getExpectedDeliveryDate());
        
//        if (entity.getApprovedBy() != null) {
//            dto.setApprovedById(entity.getApprovedBy().getId());
//            if (entity.getApprovedBy().getStaffEntity() != null) {
//                dto.setApprovedByName(entity.getApprovedBy().getStaffEntity().getFullName());
//            }
//        }
        
        dto.setInvoiceNumber(entity.getInvoiceNumber());
        dto.setTaxAmount(entity.getTaxAmount());
        dto.setShippingCost(entity.getShippingCost());
        dto.setPaymentMethod(entity.getPaymentMethod());
        dto.setDueDate(entity.getDueDate());
        dto.setPaymentDate(entity.getPaymentDate());
        
        // Set recipient and reason
        dto.setRecipient(entity.getRecipient());
        dto.setStockOutReason(entity.getStockOutReason());
        
        // Convert items
        if (entity.getSupplierTransactionItemEntities() != null) {
            List<SupplierRequestItemDTO> itemDTOs = entity.getSupplierTransactionItemEntities().stream()
                    .map(this::convertItemToDTO)
                    .collect(Collectors.toList());
            dto.setItems(itemDTOs);
        }
        
        return dto;
    }
    
    private SupplierRequestItemDTO convertItemToDTO(SupplierTransactionItemEntity entity) {
        SupplierRequestItemDTO dto = new SupplierRequestItemDTO();
        
        dto.setProductId(entity.getProductEntity().getId());
        dto.setProductName(entity.getProductEntity().getName());
        dto.setQuantity(entity.getQuantity());
        dto.setUnitPrice(entity.getUnitPrice());
        dto.setManufactureDate(entity.getManufactureDate());
        dto.setExpirationDate(entity.getExpirationDate());
        dto.setBatchNumber(entity.getBatchNumber());
        dto.setStorageLocation(entity.getStorageLocation());
        dto.setNotes(entity.getNotes());
        
        return dto;
    }
} 