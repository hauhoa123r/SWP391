package org.project.service.impl;

import org.project.entity.InventoryManagerEntity;
import org.project.model.dto.InventoryManagerDTO;
import org.project.repository.InventoryManagerRepository;
import org.project.service.InventoryManagerService;
import org.project.utils.PageUtils;
import org.project.utils.specification.PageSpecificationUtils;
import org.project.utils.specification.SpecificationUtils;
import org.project.utils.specification.search.SearchCriteria;
import org.project.utils.specification.sort.SortCriteria;
import org.project.enums.operation.ComparisonOperator;
import org.project.enums.operation.SortDirection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

@Service
public class InventoryManagerServiceImpl implements InventoryManagerService {

    @Autowired
    private InventoryManagerRepository inventoryManagerRepository;

    @Autowired
    private PageSpecificationUtils<InventoryManagerEntity> pageSpecificationUtils;

    @Autowired
    private SpecificationUtils<InventoryManagerEntity> specificationUtils;

    @Autowired
    private PageUtils<InventoryManagerEntity> pageUtils;

    @Override
    public Page<InventoryManagerDTO> getAllManagers(int page, int size, String fullName, SortDirection sortDirection, String sortField) {
        Sort sort = SortDirection.ASC.equals(sortDirection) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = pageUtils.getPageable(page, size, sort);

        List<SearchCriteria> searchCriterias = new ArrayList<>();
        if (fullName != null && !fullName.isEmpty()) {
            searchCriterias.add(new SearchCriteria("staffEntity.fullName", ComparisonOperator.CONTAINS, fullName, null));
        }

        List<SortCriteria> sortCriterias = new ArrayList<>();
        if (sortField != null && !sortField.isEmpty()) {
            sortCriterias.add(new SortCriteria(sortField, null, sortDirection, null));
        }

        Specification<InventoryManagerEntity> specification = specificationUtils.reset()
                .getSpecifications(searchCriterias, sortCriterias);

        Page<InventoryManagerEntity> managerPage = pageSpecificationUtils.getPage(specification, pageable, InventoryManagerEntity.class);

        return managerPage.map(this::convertToDTO);
    }

    @Override
    public InventoryManagerDTO getManagerById(Long id) {
        return inventoryManagerRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    private InventoryManagerDTO convertToDTO(InventoryManagerEntity entity) {
        InventoryManagerDTO dto = new InventoryManagerDTO();
        dto.setId(entity.getId());
        // Mapping các trường khác nếu cần
        return dto;
    }
}