package org.project.service;

import org.project.model.dto.InventoryManagerDTO;
import org.springframework.data.domain.Page;
import org.project.enums.operation.SortDirection;

public interface InventoryManagerService {
    Page<InventoryManagerDTO> getAllManagers(int page, int size, String fullName, SortDirection sortDirection, String sortField);
    InventoryManagerDTO getManagerById(Long id);
}