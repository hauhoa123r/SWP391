package org.project.service;


import org.project.enums.operation.SortDirection;
import org.project.model.dto.InventoryManagerDTO;
import org.springframework.data.domain.Page;

public interface InventoryManagerService {
    Page<InventoryManagerDTO> getAllManagers(int page, int size, String fullName, SortDirection sortDirection, String sortField);
    InventoryManagerDTO getManagerById(Long id);
}