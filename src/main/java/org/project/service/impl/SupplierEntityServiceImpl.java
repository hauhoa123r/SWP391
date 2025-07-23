package org.project.service.impl;

import org.project.entity.SupplierEntity;
import org.project.model.dto.SupplierEntityDTO;
import org.project.repository.SupplierEntityRepository;
import org.project.service.SupplierEntityService;
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
public class SupplierEntityServiceImpl implements SupplierEntityService {

    @Autowired
    private SupplierEntityRepository supplierEntityRepository;

    @Autowired
    private PageSpecificationUtils<SupplierEntity> pageSpecificationUtils;

    @Autowired
    private SpecificationUtils<SupplierEntity> specificationUtils;

    @Autowired
    private PageUtils<SupplierEntity> pageUtils;

    @Override
    public Page<SupplierEntityDTO> getAllSuppliers(int page, int size, String name, String email, SortDirection sortDirection, String sortField) {
        Sort sort = SortDirection.ASC.equals(sortDirection) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = pageUtils.getPageable(page, size, sort);

        List<SearchCriteria> searchCriterias = new ArrayList<>();
        if (name != null && !name.isEmpty()) {
            searchCriterias.add(new SearchCriteria("name", ComparisonOperator.CONTAINS, name, null));
        }
        if (email != null && !email.isEmpty()) {
            searchCriterias.add(new SearchCriteria("email", ComparisonOperator.CONTAINS, email, null));
        }

        List<SortCriteria> sortCriterias = new ArrayList<>();
        if (sortField != null && !sortField.isEmpty()) {
            sortCriterias.add(new SortCriteria(sortField, null, sortDirection, null));
        }

        Specification<SupplierEntity> specification = specificationUtils.reset()
                .getSpecifications(searchCriterias, sortCriterias);

        Page<SupplierEntity> supplierPage = pageSpecificationUtils.getPage(specification, pageable, SupplierEntity.class);

        return supplierPage.map(this::convertToDTO);
    }

    @Override
    public SupplierEntityDTO getSupplierById(Long id) {
        return supplierEntityRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    private SupplierEntityDTO convertToDTO(SupplierEntity entity) {
        SupplierEntityDTO dto = new SupplierEntityDTO();
        dto.setId(entity.getId());
        // Mapping các trường khác nếu cần
        return dto;
    }
}