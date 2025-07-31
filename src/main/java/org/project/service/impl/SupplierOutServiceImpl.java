package org.project.service.impl;

import org.project.entity.*;
import org.project.enums.SupplierTransactionStatus;
import org.project.enums.SupplierTransactionType;
import org.project.model.dto.SupplierOutDTO;
import org.project.model.dto.SupplierRequestItemDTO;
import org.project.repository.*;
import org.project.service.SupplierOutService;
import org.project.service.base.AbstractBaseTransactionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SupplierOutServiceImpl extends AbstractBaseTransactionServiceImpl<SupplierOutDTO> implements SupplierOutService {

    @Autowired
    public SupplierOutServiceImpl(
        SupplierTransactionRepository supplierTransactionRepository,
        SupplierEntityRepository supplierEntityRepository,
        InventoryManagerRepository inventoryManagerRepository,
        ProductRepository productRepository,
        SupplierTransactionItemRepository supplierTransactionItemRepository) {
        super(supplierTransactionRepository, supplierEntityRepository, inventoryManagerRepository,
              productRepository, supplierTransactionItemRepository, SupplierTransactionType.STOCK_OUT);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SupplierOutDTO> getFilteredTransactions(int page, int size, String status, String search, String type) {
        PageRequest pageRequest = PageRequest.of(page, size);
        
        Page<SupplierTransactionsEntity> transactions;
        
        // Apply filters
        if (search != null && !search.isEmpty()) {
            if (status != null && !status.isEmpty()) {
                try {
            SupplierTransactionStatus statusEnum = SupplierTransactionStatus.valueOf(status);
                    transactions = transactionRepository.findByTransactionTypeAndStatusAndSupplierEntityNameContainingIgnoreCase(
                            SupplierTransactionType.STOCK_OUT, statusEnum, search, pageRequest);
                } catch (IllegalArgumentException e) {
                    transactions = transactionRepository.findByTransactionTypeAndSupplierEntityNameContainingIgnoreCase(
                            SupplierTransactionType.STOCK_OUT, search, pageRequest);
                }
            } else {
                transactions = transactionRepository.findByTransactionTypeAndSupplierEntityNameContainingIgnoreCase(
                        SupplierTransactionType.STOCK_OUT, search, pageRequest);
            }
        } else if (status != null && !status.isEmpty()) {
            try {
                SupplierTransactionStatus statusEnum = SupplierTransactionStatus.valueOf(status);
                transactions = transactionRepository.findByTransactionTypeAndStatus(
                        SupplierTransactionType.STOCK_OUT, statusEnum, pageRequest);
            } catch (IllegalArgumentException e) {
                transactions = transactionRepository.findByTransactionType(SupplierTransactionType.STOCK_OUT, pageRequest);
            }
        } else {
            transactions = transactionRepository.findByTransactionType(SupplierTransactionType.STOCK_OUT, pageRequest);
        }
        
        List<SupplierOutDTO> dtos = transactions.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PageImpl<>(dtos, pageRequest, transactions.getTotalElements());
    }

    @Override
    @Transactional
    public SupplierOutDTO createTransaction(SupplierOutDTO supplierOutDTO) {
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
        SupplierEntity supplier = supplierRepository.findById(supplierOutDTO.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + supplierOutDTO.getSupplierId()));
        transaction.setSupplierEntity(supplier);
        
        // Set inventory manager
        if (supplierOutDTO.getInventoryManagerId() != null) {
            InventoryManagerEntity inventoryManager = inventoryManagerRepository.findById(supplierOutDTO.getInventoryManagerId())
                    .orElseThrow(() -> new RuntimeException("Inventory manager not found with id: " + supplierOutDTO.getInventoryManagerId()));
            transaction.setInventoryManagerEntity(inventoryManager);
        }
        
        // Save transaction to get ID
        SupplierTransactionsEntity savedTransaction = transactionRepository.save(transaction);
        
        // Add items if provided
        if (supplierOutDTO.getItems() != null && !supplierOutDTO.getItems().isEmpty()) {
            for (SupplierRequestItemDTO itemDTO : supplierOutDTO.getItems()) {
                SupplierTransactionItemEntity item = new SupplierTransactionItemEntity();
                
                // Create composite key
                SupplierTransactionItemEntityId id = new SupplierTransactionItemEntityId();
                id.setSupplierTransactionId(savedTransaction.getId());
                id.setProductId(itemDTO.getProductId());
                item.setId(id);
                
                // Set other fields
                item.setQuantity(itemDTO.getQuantity());
                item.setUnitPrice(itemDTO.getUnitPrice());
                
                // Set references
                item.setSupplierTransactionEntity(savedTransaction);
                
                ProductEntity product = productRepository.findById(itemDTO.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found with id: " + itemDTO.getProductId()));
                item.setProductEntity(product);
                
                // Save item
                itemRepository.save(item);
            }
        }
        
        return getTransactionById(savedTransaction.getId());
    }
    
    @Override
    @Transactional
    public SupplierOutDTO updateTransaction(Long id, SupplierOutDTO supplierOutDTO) {
        Optional<SupplierTransactionsEntity> transactionOpt = transactionRepository.findById(id);
        if (transactionOpt.isEmpty() || transactionOpt.get().getTransactionType() != SupplierTransactionType.STOCK_OUT) {
            return null;
        }
        
            SupplierTransactionsEntity transaction = transactionOpt.get();
            
            // Update basic information
                transaction.setStatus(supplierOutDTO.getStatus());
                transaction.setTotalAmount(supplierOutDTO.getTotalAmount());
            transaction.setNotes(supplierOutDTO.getNotes());
            transaction.setExpectedDeliveryDate(supplierOutDTO.getExpectedDeliveryDate());
            transaction.setInvoiceNumber(supplierOutDTO.getInvoiceNumber());
            transaction.setTaxAmount(supplierOutDTO.getTaxAmount());
            transaction.setShippingCost(supplierOutDTO.getShippingCost());
            transaction.setPaymentMethod(supplierOutDTO.getPaymentMethod());
            transaction.setDueDate(supplierOutDTO.getDueDate());
            transaction.setPaymentDate(supplierOutDTO.getPaymentDate());
            
        // Update specific fields
                transaction.setRecipient(supplierOutDTO.getRecipient());
            transaction.setStockOutReason(supplierOutDTO.getStockOutReason());
            
            // Update supplier if provided
            if (supplierOutDTO.getSupplierId() != null) {
            SupplierEntity supplier = supplierRepository.findById(supplierOutDTO.getSupplierId())
                        .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + supplierOutDTO.getSupplierId()));
                transaction.setSupplierEntity(supplier);
        }
        
        // Save updated transaction
        transactionRepository.save(transaction);
        
        // Return the updated DTO
        return getTransactionById(id);
    }

    @Override
    @Transactional
    public void deleteTransaction(Long id) {
        Optional<SupplierTransactionsEntity> transaction = transactionRepository.findById(id);
        if (transaction.isPresent() && transaction.get().getTransactionType() == SupplierTransactionType.STOCK_OUT) {
            transactionRepository.deleteById(id);
        }
    }
    
    @Override
    @Transactional
    public void addRejectionReason(Long id, String reason) {
        Optional<SupplierTransactionsEntity> transactionOpt = transactionRepository.findById(id);
        if (transactionOpt.isEmpty() || transactionOpt.get().getTransactionType() != SupplierTransactionType.STOCK_OUT) {
            return;
        }
        
        SupplierTransactionsEntity transaction = transactionOpt.get();
        transaction.setNotes("Rejected: " + reason + "\n\n" + (transaction.getNotes() != null ? transaction.getNotes() : ""));
        transaction.setStatus(SupplierTransactionStatus.REJECTED);
        
        transactionRepository.save(transaction);
    }
    
    @Override
    protected SupplierOutDTO convertToDTO(SupplierTransactionsEntity entity) {
        SupplierOutDTO dto = new SupplierOutDTO();
        
        dto.setId(entity.getId());
        
        // Safely set supplier details
        if (entity.getSupplierEntity() != null) {
        dto.setSupplierId(entity.getSupplierEntity().getId());
        dto.setSupplierName(entity.getSupplierEntity().getName());
        dto.setSupplierContact(entity.getSupplierEntity().getPhoneNumber());
        }
        
        // Safely set inventory manager details
        if (entity.getInventoryManagerEntity() != null) {
            dto.setInventoryManagerId(entity.getInventoryManagerEntity().getId());
            
            // Safely get staff name from inventory manager
            try {
            dto.setInventoryManagerName(entity.getInventoryManagerEntity().getStaffEntity().getFullName());
            } catch (Exception e) {
                dto.setInventoryManagerName("Unknown Manager");
            }
        }
        
        // Set transaction details
        dto.setTotalAmount(entity.getTotalAmount());
        dto.setTransactionDate(entity.getTransactionDate());
        dto.setStatus(entity.getStatus());
        dto.setApprovedDate(entity.getApprovedDate());
        dto.setNotes(entity.getNotes());
        dto.setExpectedDeliveryDate(entity.getExpectedDeliveryDate());
        dto.setInvoiceNumber(entity.getInvoiceNumber());
        dto.setTaxAmount(entity.getTaxAmount());
        dto.setShippingCost(entity.getShippingCost());
        dto.setPaymentMethod(entity.getPaymentMethod());
        dto.setDueDate(entity.getDueDate());
        dto.setPaymentDate(entity.getPaymentDate());
        dto.setRecipient(entity.getRecipient());
        dto.setStockOutReason(entity.getStockOutReason());
        
        // Set transaction type (always STOCK_OUT for this service)
        dto.setTransactionType(SupplierTransactionType.STOCK_OUT);
        
        // Set items if available
        if (entity.getSupplierTransactionItemEntities() != null && !entity.getSupplierTransactionItemEntities().isEmpty()) {
            List<SupplierRequestItemDTO> items = new ArrayList<>();
            
            for (SupplierTransactionItemEntity itemEntity : entity.getSupplierTransactionItemEntities()) {
                SupplierRequestItemDTO itemDTO = new SupplierRequestItemDTO();
                
                if (itemEntity.getId() != null) {
                    itemDTO.setProductId(itemEntity.getId().getProductId());
                }
                
                itemDTO.setQuantity(itemEntity.getQuantity());
                itemDTO.setUnitPrice(itemEntity.getUnitPrice());
                
                // Set product details if available
                if (itemEntity.getProductEntity() != null) {
                    itemDTO.setProductName(itemEntity.getProductEntity().getName());
                    
                    // Only set if these fields exist in DTO
                    if (itemEntity.getProductEntity().getProductType() != null) {
                        try {
                            itemDTO.setProductType(itemEntity.getProductEntity().getProductType());
                        } catch (Exception e) {
                            // Field might not exist, ignore
                        }
                    }
                    
                    try {
                        itemDTO.setProductUnit(itemEntity.getProductEntity().getUnit());
                    } catch (Exception e) {
                        // Field might not exist, ignore
                    }
                }
                
                items.add(itemDTO);
            }
            
            dto.setItems(items);
        }
        
        // Set type based on first product (if available)
        if (entity.getSupplierTransactionItemEntities() != null && !entity.getSupplierTransactionItemEntities().isEmpty()) {
            try {
                ProductEntity firstProduct = entity.getSupplierTransactionItemEntities().iterator().next().getProductEntity();
                if (firstProduct != null && firstProduct.getProductType() != null) {
                    dto.setType(firstProduct.getProductType().name());
                }
            } catch (Exception e) {
                dto.setType(null);
            }
        }
        
        return dto;
    }
} 