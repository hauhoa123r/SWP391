package org.project.service;

import org.project.model.dto.SupplierOutDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SupplierOutService {
    List<SupplierOutDTO> getAllSupplierOuts();
    
    Page<SupplierOutDTO> getAllSupplierOuts(int page, int size, String keyword, String status);
    
    /**
     * Get paginated supplier outs with filters
     * @param page Page number (0-based)
     * @param size Items per page
     * @param status Optional filter by status
     * @param search Optional search term for supplier name or code
     * @param type Optional filter by type (e.g. MEDICINE, EQUIPMENT)
     * @return Paginated supplier outs
     */
    Page<SupplierOutDTO> getFilteredSupplierOuts(int page, int size, String status, String search, String type);
    
    SupplierOutDTO getSupplierOutById(Long id);
    
    SupplierOutDTO createSupplierOut(SupplierOutDTO supplierOutDTO);
    
    SupplierOutDTO updateSupplierOut(Long id, SupplierOutDTO supplierOutDTO);
    
    void updateSupplierOutStatus(Long id, String status);
    
    void deleteSupplierOut(Long id);
    
    /**
     * Add rejection reason to a supplier out transaction
     * @param id Supplier out ID
     * @param reason Rejection reason
     */
    void addRejectionReason(Long id, String reason);
} 