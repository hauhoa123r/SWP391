package org.project.service;

import org.project.enums.SupplierTransactionStatus;
import org.project.model.dto.SupplierInDTO;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Service interface for supplier stock in operations
 */
public interface SupplierInService {
    /**
     * Get all supplier ins
     * @return List of supplier ins
     */
    List<SupplierInDTO> getAllSupplierIns();
    
    /**
     * Get paginated supplier ins with optional filtering by keyword and status
     * @param page Page number (0-based)
     * @param size Page size
     * @param keyword Optional search keyword
     * @param status Optional status filter
     * @return Paginated supplier ins
     */
    Page<SupplierInDTO> getAllSupplierIns(int page, int size, String keyword, String status);
    
    /**
     * Get paginated supplier ins with filtering options
     * @param page Page number (0-based)
     * @param size Page size
     * @param status Optional status filter
     * @param search Optional search term
     * @param type Optional type filter
     * @return Paginated supplier ins
     */
    Page<SupplierInDTO> getFilteredSupplierIns(int page, int size, String status, String search, String type);
    
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
     * Get supplier in by ID
     * @param id Supplier in ID
     * @return Supplier in DTO or null if not found
     */
    SupplierInDTO getSupplierInById(Long id);
    
    /**
     * Create supplier in
     * @param supplierInDTO Supplier in DTO
     * @return Created supplier in
     */
    SupplierInDTO createSupplierIn(SupplierInDTO supplierInDTO);
    
    /**
     * Update supplier in
     * @param id Supplier in ID
     * @param supplierInDTO Supplier in DTO
     * @return Updated supplier in
     */
    SupplierInDTO updateSupplierIn(Long id, SupplierInDTO supplierInDTO);
    
    /**
     * Update supplier in status
     * @param id Supplier in ID
     * @param status New status
     */
    void updateSupplierInStatus(Long id, String status);
    
    /**
     * Delete supplier in
     * @param id Supplier in ID
     */
    void deleteSupplierIn(Long id);
    
    /**
     * Add rejection reason to a supplier in transaction
     * @param id Supplier in ID
     * @param reason Rejection reason
     */
    void addRejectionReason(Long id, String reason);
} 