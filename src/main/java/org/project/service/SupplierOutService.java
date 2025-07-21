package org.project.service;

import org.project.model.dto.SupplierOutDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SupplierOutService {
    List<SupplierOutDTO> getAllSupplierOuts();
    
    Page<SupplierOutDTO> getAllSupplierOuts(int page, int size, String keyword, String status);
    
    SupplierOutDTO getSupplierOutById(Long id);
    
    SupplierOutDTO createSupplierOut(SupplierOutDTO supplierOutDTO);
    
    SupplierOutDTO updateSupplierOut(Long id, SupplierOutDTO supplierOutDTO);
    
    void updateSupplierOutStatus(Long id, String status);
    
    void deleteSupplierOut(Long id);
} 