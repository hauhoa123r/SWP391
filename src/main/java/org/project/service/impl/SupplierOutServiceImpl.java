package org.project.service.impl;

import org.project.entity.InventoryManagerEntity;
import org.project.entity.ProductEntity;
import org.project.entity.SupplierEntity;
import org.project.entity.SupplierTransactionItemEntity;
import org.project.entity.SupplierTransactionItemEntityId;
import org.project.entity.SupplierTransactionsEntity;
import org.project.enums.SupplierTransactionStatus;
import org.project.enums.SupplierTransactionType;
import org.project.model.dto.SupplierInDTO;
import org.project.model.dto.SupplierOutDTO;
import org.project.model.dto.SupplierRequestItemDTO;
import org.project.repository.InventoryManagerRepository;
import org.project.repository.ProductRepository;
import org.project.repository.SupplierEntityRepository;
import org.project.repository.SupplierTransactionItemRepository;
import org.project.repository.SupplierTransactionRepository;
import org.project.service.SupplierOutService;
import org.project.service.base.AbstractBaseTransactionServiceImpl;
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
import java.util.*;
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
        transaction.setStatus(supplierOutDTO.getStatus() != null ? supplierOutDTO.getStatus() : SupplierTransactionStatus.PREPARING);
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
    public Page<SupplierInDTO> getFilteredSupplierInsForStockIn(int page, int size, String status, String search, String type, List<SupplierTransactionStatus> allowedStatuses) {
        return null;
    }

    @Override
    protected SupplierOutDTO convertToDTO(SupplierTransactionsEntity entity) {
        SupplierOutDTO dto = new SupplierOutDTO();
        
        // Set basic information
        dto.setId(entity.getId());
        dto.setSupplierId(entity.getSupplierEntity().getId());
        dto.setSupplierName(entity.getSupplierEntity().getName());
        dto.setInventoryManagerId(entity.getInventoryManagerEntity().getId());
        // TODO: Fix this if inventoryManagerEntity.getUser() is needed
        dto.setInventoryManagerName(null); 
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
        
        // Set specific StockOut fields
        dto.setRecipient(entity.getRecipient());
        dto.setStockOutReason(entity.getStockOutReason());
        dto.setTransactionType(entity.getTransactionType());
        
        // Set items
        if (entity.getSupplierTransactionItemEntities() != null && !entity.getSupplierTransactionItemEntities().isEmpty()) {
            List<SupplierRequestItemDTO> items = new ArrayList<>();
            
            for (SupplierTransactionItemEntity itemEntity : entity.getSupplierTransactionItemEntities()) {
                SupplierRequestItemDTO itemDTO = new SupplierRequestItemDTO();
                
                // Map item fields
                itemDTO.setProductId(itemEntity.getProductEntity().getId());
                itemDTO.setProductName(itemEntity.getProductEntity().getName());
                itemDTO.setQuantity(itemEntity.getQuantity());
                itemDTO.setUnitPrice(itemEntity.getUnitPrice());
                
                // Set product type if available
                if (itemEntity.getProductEntity() != null && itemEntity.getProductEntity().getProductType() != null) {
                    itemDTO.setProductType(itemEntity.getProductEntity().getProductType());
                }
                
                items.add(itemDTO);
            }
            
            dto.setItems(items);
            
            // Determine and set the type field based on items
            dto.setType(determineProductType(entity.getSupplierTransactionItemEntities()));
        } else {
            dto.setItems(new ArrayList<>());
            // Set default type for orders without items
            dto.setType("MEDICAL_PRODUCT");
        }
        
        return dto;
    }
    
    /**
     * Determine the product type for an order based on its items
     * @param items The collection of items in the order
     * @return The determined product type (MEDICINE or MEDICAL_PRODUCT)
     */
    private String determineProductType(Set<SupplierTransactionItemEntity> items) {
        if (items == null || items.isEmpty()) {
            return null;
        }

        return items.stream()
                .filter(item -> item != null
                        && item.getProductEntity() != null
                        && item.getProductEntity().getProductType() != null)
                .map(item -> item.getProductEntity().getProductType().name())
                .findFirst()
                .orElse(null);
    }

} 