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
import org.project.model.dto.SupplierRequestItemDTO;
import org.project.repository.InventoryManagerRepository;
import org.project.repository.ProductRepository;
import org.project.repository.SupplierEntityRepository;
import org.project.repository.SupplierTransactionItemRepository;
import org.project.repository.SupplierTransactionRepository;
import org.project.service.SupplierInService;
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
public class SupplierInServiceImpl extends AbstractBaseTransactionServiceImpl<SupplierInDTO> implements SupplierInService {

    @Autowired
    public SupplierInServiceImpl(
        SupplierTransactionRepository supplierTransactionRepository,
        SupplierEntityRepository supplierEntityRepository,
        InventoryManagerRepository inventoryManagerRepository,
        ProductRepository productRepository,
        SupplierTransactionItemRepository supplierTransactionItemRepository) {
        super(supplierTransactionRepository, supplierEntityRepository, inventoryManagerRepository,
              productRepository, supplierTransactionItemRepository, SupplierTransactionType.STOCK_IN);
    }

    @Override
    public Page<SupplierInDTO> getFilteredSupplierInsForStockIn(int page, int size, String status, String search, 
                                                            String type, List<SupplierTransactionStatus> allowedStatuses) {
        return getFilteredTransactions(page, size, search, status, allowedStatuses, SupplierTransactionType.STOCK_IN);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<SupplierInDTO> getFilteredTransactions(int page, int size, String status, String search, String type) {
        PageRequest pageRequest = PageRequest.of(page, size);
        
        Page<SupplierTransactionsEntity> transactions;
        
        // Apply filters
                if (search != null && !search.isEmpty()) {
            if (status != null && !status.isEmpty()) {
                try {
                    SupplierTransactionStatus statusEnum = SupplierTransactionStatus.valueOf(status);
                    transactions = transactionRepository.findByTransactionTypeAndStatusAndSupplierEntityNameContainingIgnoreCase(
                            SupplierTransactionType.STOCK_IN, statusEnum, search, pageRequest);
                } catch (IllegalArgumentException e) {
                    transactions = transactionRepository.findByTransactionTypeAndSupplierEntityNameContainingIgnoreCase(
                            SupplierTransactionType.STOCK_IN, search, pageRequest);
                }
            } else {
                transactions = transactionRepository.findByTransactionTypeAndSupplierEntityNameContainingIgnoreCase(
                        SupplierTransactionType.STOCK_IN, search, pageRequest);
            }
        } else if (status != null && !status.isEmpty()) {
            try {
                SupplierTransactionStatus statusEnum = SupplierTransactionStatus.valueOf(status);
                transactions = transactionRepository.findByTransactionTypeAndStatus(
                        SupplierTransactionType.STOCK_IN, statusEnum, pageRequest);
            } catch (IllegalArgumentException e) {
                transactions = transactionRepository.findByTransactionType(SupplierTransactionType.STOCK_IN, pageRequest);
            }
        } else {
            transactions = transactionRepository.findByTransactionType(SupplierTransactionType.STOCK_IN, pageRequest);
        }
        
        List<SupplierInDTO> dtos = transactions.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PageImpl<>(dtos, pageRequest, transactions.getTotalElements());
    }

    @Override
    @Transactional
    public SupplierInDTO createTransaction(SupplierInDTO supplierInDTO) {
        SupplierTransactionsEntity transaction = new SupplierTransactionsEntity();
        
        // Set basic information
        transaction.setTransactionType(SupplierTransactionType.STOCK_IN);
        transaction.setStatus(supplierInDTO.getStatus() != null ? supplierInDTO.getStatus() : SupplierTransactionStatus.PENDING);
        transaction.setTransactionDate(supplierInDTO.getTransactionDate() != null ? supplierInDTO.getTransactionDate() : Timestamp.from(Instant.now()));
        transaction.setTotalAmount(supplierInDTO.getTotalAmount());
        transaction.setNotes(supplierInDTO.getNotes());
        transaction.setExpectedDeliveryDate(supplierInDTO.getExpectedDeliveryDate());
        transaction.setInvoiceNumber(supplierInDTO.getInvoiceNumber());
        transaction.setTaxAmount(supplierInDTO.getTaxAmount());
        transaction.setShippingCost(supplierInDTO.getShippingCost());
        transaction.setPaymentMethod(supplierInDTO.getPaymentMethod());
        transaction.setDueDate(supplierInDTO.getDueDate());
        transaction.setPaymentDate(supplierInDTO.getPaymentDate());
        
        // Set supplier
        if (supplierInDTO.getSupplierId() != null) {
            SupplierEntity supplier = supplierRepository.findById(supplierInDTO.getSupplierId())
                    .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + supplierInDTO.getSupplierId()));
            transaction.setSupplierEntity(supplier);
        }
        
        // Set inventory manager
        if (supplierInDTO.getInventoryManagerId() != null) {
            InventoryManagerEntity inventoryManager = inventoryManagerRepository.findById(supplierInDTO.getInventoryManagerId())
                    .orElseThrow(() -> new RuntimeException("Inventory manager not found with id: " + supplierInDTO.getInventoryManagerId()));
            transaction.setInventoryManagerEntity(inventoryManager);
        }
        
        // Save transaction to get ID
        SupplierTransactionsEntity savedTransaction = transactionRepository.save(transaction);
        
        // Add transaction items if provided
        if (supplierInDTO.getItems() != null && !supplierInDTO.getItems().isEmpty()) {
            for (SupplierRequestItemDTO itemDTO : supplierInDTO.getItems()) {
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
    public SupplierInDTO updateTransaction(Long id, SupplierInDTO supplierInDTO) {
        Optional<SupplierTransactionsEntity> transactionOpt = transactionRepository.findById(id);
        if (transactionOpt.isEmpty() || transactionOpt.get().getTransactionType() != SupplierTransactionType.STOCK_IN) {
            return null;
        }
        
        SupplierTransactionsEntity transaction = transactionOpt.get();
        
        // Update basic information
        transaction.setStatus(supplierInDTO.getStatus());
        transaction.setTotalAmount(supplierInDTO.getTotalAmount());
        transaction.setNotes(supplierInDTO.getNotes());
        transaction.setExpectedDeliveryDate(supplierInDTO.getExpectedDeliveryDate());
        transaction.setInvoiceNumber(supplierInDTO.getInvoiceNumber());
        transaction.setTaxAmount(supplierInDTO.getTaxAmount());
        transaction.setShippingCost(supplierInDTO.getShippingCost());
        transaction.setPaymentMethod(supplierInDTO.getPaymentMethod());
        transaction.setDueDate(supplierInDTO.getDueDate());
        transaction.setPaymentDate(supplierInDTO.getPaymentDate());
        
        // Update supplier if provided
        if (supplierInDTO.getSupplierId() != null) {
            SupplierEntity supplier = supplierRepository.findById(supplierInDTO.getSupplierId())
                    .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + supplierInDTO.getSupplierId()));
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
        if (transaction.isPresent() && transaction.get().getTransactionType() == SupplierTransactionType.STOCK_IN) {
            transactionRepository.deleteById(id);
        }
    }
    
    @Override
    @Transactional
    public void addRejectionReason(Long id, String reason) {
        Optional<SupplierTransactionsEntity> transactionOpt = transactionRepository.findById(id);
        if (transactionOpt.isEmpty() || transactionOpt.get().getTransactionType() != SupplierTransactionType.STOCK_IN) {
            return;
        }
        
        SupplierTransactionsEntity transaction = transactionOpt.get();
        transaction.setNotes("Rejected: " + reason + "\n\n" + (transaction.getNotes() != null ? transaction.getNotes() : ""));
        transaction.setStatus(SupplierTransactionStatus.REJECTED);
        
        transactionRepository.save(transaction);
    }

    @Override
    protected SupplierInDTO convertToDTO(SupplierTransactionsEntity entity) {
        SupplierInDTO dto = new SupplierInDTO();
        
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

        // Set transaction type (always STOCK_IN for this service)
        dto.setTransactionType(SupplierTransactionType.STOCK_IN);
        
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
                    itemDTO.setProductType(itemEntity.getProductEntity().getProductType());
                    itemDTO.setProductUnit(itemEntity.getProductEntity().getUnit());
                }
                
                items.add(itemDTO);
            }
            
            dto.setItems(items);
        }
        
        // Set type based on first product (if available)
        Optional<String> typeOptional = entity.getSupplierTransactionItemEntities().stream()
                .filter(item -> item != null
                        && item.getProductEntity() != null
                        && item.getProductEntity().getProductType() != null)
                .map(item -> item.getProductEntity().getProductType().name())
                .findFirst();

        dto.setType(typeOptional.orElse(null));
        
        return dto;
    }
} 