package org.project.service;

import org.project.enums.SupplierTransactionStatus;
import org.project.model.dto.SupplierInDTO;
import org.project.service.base.BaseSupplierTransactionService;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Service interface for supplier stock in operations
 */
public interface SupplierInService extends BaseSupplierTransactionService<SupplierInDTO> {
    
    /**
     * Get all supplier ins
     * @return List of supplier ins
     */
    @Override
    List<SupplierInDTO> getAllTransactions();
    
    /**
     * Get paginated supplier ins with optional filtering by keyword and status
     * @param page Page number (0-based)
     * @param size Page size
     * @param keyword Optional search keyword
     * @param status Optional status filter
     * @return Paginated supplier ins
     */
    @Override
    Page<SupplierInDTO> getAllTransactions(int page, int size, String keyword, String status);
    
    /**
     * Get paginated supplier ins with filtering options
     * @param page Page number (0-based)
     * @param size Page size
     * @param status Optional status filter
     * @param search Optional search term
     * @param type Optional type filter
     * @return Paginated supplier ins
     */
    @Override
    Page<SupplierInDTO> getFilteredTransactions(int page, int size, String status, String search, String type);
    
    /**
     * Get paginated supplier ins filtered by specific statuses
     * @param page Page number (0-based)
     * @param size Page size
     * @param status Optional specific status filter
     * @param search Optional search term
     * @param type Optional type filter
     * @param allowedStatuses List of allowed statuses to include
     * @return Paginated supplier ins
     */
    Page<SupplierInDTO> getFilteredSupplierInsForStockIn(int page, int size, String status, String search, 
                                                      String type, List<SupplierTransactionStatus> allowedStatuses);
    
    /**
     * Add rejection reason to a supplier in transaction
     * @param id Supplier in ID
     * @param reason Rejection reason
     */
    void addRejectionReason(Long id, String reason);
    
    // Phương thức tương thích ngược cho các controller cũ
    
    /**
     * @deprecated Sử dụng {@link #getAllTransactions()} thay thế
     */
    @Deprecated
    default List<SupplierInDTO> getAllSupplierIns() {
        return getAllTransactions();
    }
    
    /**
     * @deprecated Sử dụng {@link #getAllTransactions(int, int, String, String)} thay thế
     */
    @Deprecated
    default Page<SupplierInDTO> getAllSupplierIns(int page, int size, String keyword, String status) {
        return getAllTransactions(page, size, keyword, status);
    }
    
    /**
     * @deprecated Sử dụng {@link #getFilteredTransactions(int, int, String, String, String)} thay thế
     */
    @Deprecated
    default Page<SupplierInDTO> getFilteredSupplierIns(int page, int size, String status, String search, String type) {
        return getFilteredTransactions(page, size, status, search, type);
    }
    
    /**
     * @deprecated Sử dụng {@link #getTransactionById(Long)} thay thế
     */
    @Deprecated
    default SupplierInDTO getSupplierInById(Long id) {
        return getTransactionById(id);
    }
    
    /**
     * @deprecated Sử dụng {@link #createTransaction(SupplierInDTO)} thay thế
     */
    @Deprecated
    default SupplierInDTO createSupplierIn(SupplierInDTO supplierInDTO) {
        return createTransaction(supplierInDTO);
    }
    
    /**
     * @deprecated Sử dụng {@link #updateTransaction(Long, SupplierInDTO)} thay thế
     */
    @Deprecated
    default SupplierInDTO updateSupplierIn(Long id, SupplierInDTO supplierInDTO) {
        return updateTransaction(id, supplierInDTO);
    }
    
    /**
     * @deprecated Sử dụng {@link #updateTransactionStatus(Long, String)} thay thế
     */
    @Deprecated
    default SupplierInDTO updateSupplierInStatus(Long id, String status) {
        return updateTransactionStatus(id, status);
    }
    
    /**
     * @deprecated Sử dụng {@link #deleteTransaction(Long)} thay thế
     */
    @Deprecated
    default void deleteSupplierIn(Long id) {
        deleteTransaction(id);
    }
} 