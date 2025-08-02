package org.project.service;

import org.project.model.dto.SupplierOutDTO;
import org.project.service.base.BaseSupplierTransactionService;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SupplierOutService extends BaseSupplierTransactionService<SupplierOutDTO> {
    
    @Override
    List<SupplierOutDTO> getAllTransactions();
    
    @Override
    Page<SupplierOutDTO> getAllTransactions(int page, int size, String keyword, String status);
    
    @Override
    Page<SupplierOutDTO> getFilteredTransactions(int page, int size, String status, String search, String type);
    
    // Phương thức tương thích ngược cho các controller cũ
    
    @Deprecated
    default List<SupplierOutDTO> getAllSupplierOuts() {
        return getAllTransactions();
    }
    
    @Deprecated
    default Page<SupplierOutDTO> getAllSupplierOuts(int page, int size, String keyword, String status) {
        return getAllTransactions(page, size, keyword, status);
    }
    
    @Deprecated
    default Page<SupplierOutDTO> getFilteredSupplierOuts(int page, int size, String status, String search, String type) {
        return getFilteredTransactions(page, size, status, search, type);
    }
    
    @Deprecated
    default SupplierOutDTO getSupplierOutById(Long id) {
        return getTransactionById(id);
    }
    
    @Deprecated
    default SupplierOutDTO createSupplierOut(SupplierOutDTO supplierOutDTO) {
        return createTransaction(supplierOutDTO);
    }
    
    @Deprecated
    default SupplierOutDTO updateSupplierOut(Long id, SupplierOutDTO supplierOutDTO) {
        return updateTransaction(id, supplierOutDTO);
    }
    
    @Deprecated
    default SupplierOutDTO updateSupplierOutStatus(Long id, String status) {
        return updateTransactionStatus(id, status);
    }
    
    @Deprecated
    default void deleteSupplierOut(Long id) {
        deleteTransaction(id);
    }
    
    @Deprecated
    default void addRejectionReason(Long id, String reason) {
        // Implement in service class
    }
}