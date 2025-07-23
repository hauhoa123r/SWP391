package org.project.service.impl;

import jakarta.persistence.criteria.JoinType;
import jakarta.transaction.Transactional;
import org.project.converter.HospitalConverter;
import org.project.entity.HospitalEntity;
import org.project.enums.operation.ComparisonOperator;
import org.project.enums.operation.SortDirection;
import org.project.exception.page.InvalidPageException;
import org.project.exception.page.PageNotFoundException;
import org.project.exception.sql.EntityNotFoundException;
import org.project.model.dto.HospitalDTO;
import org.project.model.response.HospitalResponse;
import org.project.repository.HospitalRepository;
import org.project.service.HospitalService;
import org.project.utils.PageUtils;
import org.project.utils.specification.SpecificationUtils;
import org.project.utils.specification.search.SearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class HospitalServiceImpl implements HospitalService {

    private HospitalRepository hospitalRepository;
    private HospitalConverter hospitalConverter;
    private PageUtils<HospitalEntity> pageUtils;
    private SpecificationUtils<HospitalEntity> specificationUtils;

    @Autowired
    public void setHospitalRepository(HospitalRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
    }

    @Autowired
    public void setHospitalConverter(HospitalConverter hospitalConverter) {
        this.hospitalConverter = hospitalConverter;
    }

    @Autowired
    public void setPageUtils(PageUtils<HospitalEntity> pageUtils) {
        this.pageUtils = pageUtils;
    }

    @Autowired
    public void setSpecificationUtils(SpecificationUtils<HospitalEntity> specificationUtils) {
        this.specificationUtils = specificationUtils;
    }

    @Override
    public HospitalResponse getHospital(Long id) {
        return hospitalRepository.findById(id)
                .map(hospitalConverter::toResponse)
                .orElseThrow(() -> new EntityNotFoundException(HospitalEntity.class, id));
    }

    @Override
    public Page<HospitalResponse> getHospitals(int index, int size, HospitalDTO hospitalDTO) {
        Sort sort = Sort.unsorted();
        if (hospitalDTO.getSortFieldName() != null && !hospitalDTO.getSortFieldName().isEmpty()) {
            sort = Sort.by((SortDirection.ASC == hospitalDTO.getSortDirection() ? Sort.Direction.ASC : Sort.Direction.DESC), hospitalDTO.getSortFieldName());
        }
        Pageable pageable = pageUtils.getPageable(index, size, sort);
        Page<HospitalEntity> hospitalEntityPage = hospitalRepository.findAll(specificationUtils.reset().getSearchSpecifications(new SearchCriteria(HospitalEntity.Fields.name, ComparisonOperator.CONTAINS, hospitalDTO.getName(), JoinType.INNER), new SearchCriteria(HospitalEntity.Fields.address, ComparisonOperator.CONTAINS, hospitalDTO.getAddress(), JoinType.INNER)), pageable);
        pageUtils.validatePage(hospitalEntityPage, HospitalEntity.class);
        return hospitalEntityPage.map(hospitalConverter::toResponse);
    }

    @Override
    public Page<HospitalResponse> getHospitals(int index, int size) {
        Pageable pageable = pageUtils.getPageable(index, size);
        Page<HospitalEntity> hospitalEntityPage = hospitalRepository.findAll(pageable);
        pageUtils.validatePage(hospitalEntityPage, HospitalEntity.class);
        return hospitalEntityPage.map(hospitalConverter::toResponse);
    }

    @Override
    public Page<HospitalResponse> searchHospitalsByKeyword(int index, int size, String keyword) {
        if (index < 0 || size <= 0) {
            throw new InvalidPageException(index, size);
        }
        Pageable pageable = PageRequest.of(index, size);
        Page<HospitalEntity> hospitalEntityPage = hospitalRepository.findAllByNameContainingIgnoreCase(keyword, pageable);
        if (hospitalEntityPage.isEmpty()) {
            throw new PageNotFoundException(HospitalEntity.class, index, size);
        }
        return hospitalEntityPage.map(hospitalConverter::toResponse);
    }
}
