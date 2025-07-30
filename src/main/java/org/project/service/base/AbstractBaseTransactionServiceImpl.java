package org.project.service.base;

import org.project.entity.SupplierTransactionsEntity;
import org.project.enums.SupplierTransactionStatus;
import org.project.enums.SupplierTransactionType;
import org.project.model.dto.SupplierInDTO;
import org.project.repository.InventoryManagerRepository;
import org.project.repository.ProductRepository;
import org.project.repository.SupplierEntityRepository;
import org.project.repository.SupplierTransactionItemRepository;
import org.project.repository.SupplierTransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Abstract class implementing common methods of BaseSupplierTransactionService.
 * 
 * @param <T> DTO type (SupplierInDTO or SupplierOutDTO)
 */
public abstract class AbstractBaseTransactionServiceImpl<T> extends BaseTransactionService implements BaseSupplierTransactionService<T> {
    
    protected final SupplierTransactionType transactionType;
    
    protected AbstractBaseTransactionServiceImpl(
            SupplierTransactionRepository transactionRepository,
            SupplierEntityRepository supplierRepository,
            InventoryManagerRepository inventoryManagerRepository,
            ProductRepository productRepository,
            SupplierTransactionItemRepository itemRepository,
            SupplierTransactionType transactionType) {
        super(transactionRepository, supplierRepository, inventoryManagerRepository, productRepository, itemRepository);
        this.transactionType = transactionType;
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> getAllTransactions() {
        List<SupplierTransactionsEntity> transactions = transactionRepository.findByTransactionType(transactionType);
        return transactions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<T> getAllTransactions(int page, int size, String keyword, String status) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<SupplierTransactionsEntity> transactions;
        
        if (keyword != null && !keyword.isEmpty()) {
            if (status != null && !status.isEmpty()) {
                try {
                    SupplierTransactionStatus statusEnum = SupplierTransactionStatus.valueOf(status);
                    transactions = transactionRepository.findByTransactionTypeAndStatusAndSupplierEntityNameContainingIgnoreCase(
                            transactionType, statusEnum, keyword, pageRequest);
                } catch (IllegalArgumentException e) {
                    transactions = transactionRepository.findByTransactionTypeAndSupplierEntityNameContainingIgnoreCase(
                            transactionType, keyword, pageRequest);
                }
            } else {
                transactions = transactionRepository.findByTransactionTypeAndSupplierEntityNameContainingIgnoreCase(
                        transactionType, keyword, pageRequest);
            }
        } else if (status != null && !status.isEmpty()) {
            try {
                SupplierTransactionStatus statusEnum = SupplierTransactionStatus.valueOf(status);
                transactions = transactionRepository.findByTransactionTypeAndStatus(
                        transactionType, statusEnum, pageRequest);
            } catch (IllegalArgumentException e) {
                transactions = transactionRepository.findByTransactionType(transactionType, pageRequest);
            }
        } else {
            transactions = transactionRepository.findByTransactionType(transactionType, pageRequest);
        }
        
        List<T> dtos = transactions.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PageImpl<>(dtos, pageRequest, transactions.getTotalElements());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<T> getFilteredTransactions(int page, int size, String keyword, String status, 
                                          List<SupplierTransactionStatus> allowedStatuses,
                                          SupplierTransactionType transactionType) {
        Page<SupplierTransactionsEntity> transactionsPage = getFilteredTransactionsEntities(
                page, size, keyword, status, allowedStatuses, transactionType);
        
        List<T> dtos = transactionsPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PageImpl<>(dtos, PageRequest.of(page, size), transactionsPage.getTotalElements());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<T> getFilteredTransactionsForView(int page, int size, String status, String search, 
                                              String type, List<SupplierTransactionStatus> allowedStatuses) {
        return getFilteredTransactions(page, size, search, status, allowedStatuses, transactionType);
    }

    @Override
    @Transactional(readOnly = true)
    public T getTransactionById(Long id) {
        Optional<SupplierTransactionsEntity> transactionOpt = transactionRepository.findById(id);
        return transactionOpt.filter(t -> t.getTransactionType() == transactionType)
                .map(this::convertToDTO)
                .orElse(null);
    }
    
    @Override
    @Transactional
    public T updateTransactionStatus(Long id, String status) {
        SupplierTransactionsEntity transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
        
        if (transaction.getTransactionType() != transactionType) {
            throw new RuntimeException("Transaction is not of type: " + transactionType);
        }
        
        // Validate current status against new status
        SupplierTransactionStatus currentStatus = transaction.getStatus();
        SupplierTransactionStatus newStatus = SupplierTransactionStatus.valueOf(status);
        
        if (!isValidStatusTransition(currentStatus, newStatus)) {
            throw new RuntimeException(
                    "Invalid status transition from " + currentStatus + " to " + newStatus);
        }
        
        transaction.setStatus(newStatus);
        
        // If status is COMPLETED, set approvedDate
        if (SupplierTransactionStatus.COMPLETED.name().equals(status)) {
            transaction.setApprovedDate(Timestamp.from(Instant.now()));
        }
        
        SupplierTransactionsEntity updatedTransaction = transactionRepository.save(transaction);
        return convertToDTO(updatedTransaction);
    }

    public abstract Page<SupplierInDTO> getFilteredSupplierInsForStockIn(int page, int size, String status, String search,
                                                                         String type, List<SupplierTransactionStatus> allowedStatuses);

    /**
     * Chuyển đổi từ Entity sang DTO
     * @param entity Entity cần chuyển đổi
     * @return DTO tương ứng
     */
    protected abstract T convertToDTO(SupplierTransactionsEntity entity);
} 