package org.project.service;

import org.project.model.dto.SupplierOutDTO;
import org.project.service.base.BaseSupplierTransactionService;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Service interface for supplier stock out operations
 */
public interface SupplierOutService extends BaseSupplierTransactionService<SupplierOutDTO> {
    
    /**
     * Get all supplier outs
     * @return List of supplier outs
     */
    @Override
    List<SupplierOutDTO> getAllTransactions();
    
    /**
     * Get paginated supplier outs with filters
     * @param page Page number (0-based)
     * @param size Items per page
     * @param keyword Optional search term
     * @param status Optional filter by status
     * @return Paginated supplier outs
     */
    @Override
    Page<SupplierOutDTO> getAllTransactions(int page, int size, String keyword, String status);
    
    /**
     * Get paginated supplier outs with filters
     * @param page Page number (0-based)
     * @param size Items per page
     * @param status Optional filter by status
     * @param search Optional search term for supplier name or code
     * @param type Optional filter by type (e.g. MEDICINE, EQUIPMENT)
     * @return Paginated supplier outs
     */
    @Override
    Page<SupplierOutDTO> getFilteredTransactions(int page, int size, String status, String search, String type);
    
    // Phương thức tương thích ngược cho các controller cũ
    
    /**
     * @deprecated Sử dụng {@link #getAllTransactions()} thay thế
     */
    @Deprecated
    default List<SupplierOutDTO> getAllSupplierOuts() {
        return getAllTransactions();
    }
    
    /**
     * @deprecated Sử dụng {@link #getAllTransactions(int, int, String, String)} thay thế
     */
    @Deprecated
    default Page<SupplierOutDTO> getAllSupplierOuts(int page, int size, String keyword, String status) {
        return getAllTransactions(page, size, keyword, status);
    }
    
    /**
     * @deprecated Sử dụng {@link #getFilteredTransactions(int, int, String, String, String)} thay thế
     */
    @Deprecated
    default Page<SupplierOutDTO> getFilteredSupplierOuts(int page, int size, String status, String search, String type) {
        return getFilteredTransactions(page, size, status, search, type);
    }
    
    /**
     * @deprecated Sử dụng {@link #getTransactionById(Long)} thay thế
     */
    @Deprecated
    default SupplierOutDTO getSupplierOutById(Long id) {
        return getTransactionById(id);
    }
    
    /**
     * @deprecated Sử dụng {@link #createTransaction(SupplierOutDTO)} thay thế
     */
    @Deprecated
    default SupplierOutDTO createSupplierOut(SupplierOutDTO supplierOutDTO) {
        return createTransaction(supplierOutDTO);
    }
    
    /**
     * @deprecated Sử dụng {@link #updateTransaction(Long, SupplierOutDTO)} thay thế
     */
    @Deprecated
    default SupplierOutDTO updateSupplierOut(Long id, SupplierOutDTO supplierOutDTO) {
        return updateTransaction(id, supplierOutDTO);
    }
    
    /**
     * @deprecated Sử dụng {@link #updateTransactionStatus(Long, String)} thay thế
     */
    @Deprecated
    default SupplierOutDTO updateSupplierOutStatus(Long id, String status) {
        return updateTransactionStatus(id, status);
    }
    
    /**
     * @deprecated Sử dụng {@link #deleteTransaction(Long)} thay thế
     */
    @Deprecated
    default void deleteSupplierOut(Long id) {
        deleteTransaction(id);
    }
    
    /**
     * @deprecated Sử dụng {@link #addRejectionReason(Long, String)} thay thế
     */
    @Deprecated
    default void addRejectionReason(Long id, String reason) {
        // Implement in service class
    }
} 