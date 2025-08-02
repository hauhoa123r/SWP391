package org.project.service;

import org.project.entity.SupplierTransactionsEntity;
import org.project.enums.SupplierTransactionStatus;
import org.project.model.dto.SupplierInDTO;
import org.project.service.base.BaseSupplierTransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SupplierInService extends BaseSupplierTransactionService<SupplierInDTO> {
    
    @Override
    List<SupplierInDTO> getAllTransactions();
    
    @Override
    Page<SupplierInDTO> getAllTransactions(int page, int size, String keyword, String status);
    
    @Override
    Page<SupplierInDTO> getFilteredTransactions(int page, int size, String status, String search, String type);
    
    Page<SupplierInDTO> getFilteredSupplierInsForStockIn(Pageable pageable, String status, String search, String type, List<SupplierTransactionStatus> allowedStatuses);
    
    void addRejectionReason(Long id, String reason);
    
    // Phương thức tương thích ngược cho các controller cũ
    
    @Deprecated
    default List<SupplierInDTO> getAllSupplierIns() {
        return getAllTransactions();
    }
    
    @Deprecated
    default Page<SupplierInDTO> getAllSupplierIns(int page, int size, String keyword, String status) {
        return getAllTransactions(page, size, keyword, status);
    }
    
    @Deprecated
    default Page<SupplierInDTO> getFilteredSupplierIns(int page, int size, String status, String search, String type) {
        return getFilteredTransactions(page, size, status, search, type);
    }
    
    @Deprecated
    default SupplierInDTO getSupplierInById(Long id) {
        return getTransactionById(id);
    }
    
    @Deprecated
    default SupplierInDTO createSupplierIn(SupplierInDTO supplierInDTO) {
        return createTransaction(supplierInDTO);
    }
    
    @Deprecated
    default SupplierInDTO updateSupplierIn(Long id, SupplierInDTO supplierInDTO) {
        return updateTransaction(id, supplierInDTO);
    }
    
    @Deprecated
    default SupplierInDTO updateSupplierInStatus(Long id, String status) {
        return updateTransactionStatus(id, status);
    }
    
    @Deprecated
    default void deleteSupplierIn(Long id) {
        deleteTransaction(id);
    }

    SupplierInDTO convertToDTO(SupplierTransactionsEntity entity);
} 