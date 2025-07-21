package org.project.service;

import org.project.model.dto.SupplierInDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SupplierInService {
    List<SupplierInDTO> getAllSupplierIns();
    
    Page<SupplierInDTO> getAllSupplierIns(int page, int size, String keyword, String status);
    
    SupplierInDTO getSupplierInById(Long id);
    
    SupplierInDTO createSupplierIn(SupplierInDTO supplierInDTO);
    
    SupplierInDTO updateSupplierIn(Long id, SupplierInDTO supplierInDTO);
    
    void updateSupplierInStatus(Long id, String status);
    
    void deleteSupplierIn(Long id);
} 