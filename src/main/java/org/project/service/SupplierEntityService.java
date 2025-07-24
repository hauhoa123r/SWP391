package org.project.service;

import org.project.model.dto.SupplierEntityDTO;
import org.project.enums.operation.SortDirection;
import org.springframework.data.domain.Page;

public interface SupplierEntityService {
    Page<SupplierEntityDTO> getAllSuppliers(int page, int size, String name, String email, SortDirection sortDirection, String sortField);
    SupplierEntityDTO getSupplierById(Long id);
}