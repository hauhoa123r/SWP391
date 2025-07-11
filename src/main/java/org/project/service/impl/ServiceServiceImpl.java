package org.project.service.impl;

import jakarta.persistence.criteria.JoinType;
import jakarta.transaction.Transactional;
import org.project.config.WebConstant;
import org.project.converter.ServiceConverter;
import org.project.entity.ServiceEntity;
import org.project.enums.operation.AggregationFunction;
import org.project.enums.operation.ComparisonOperator;
import org.project.exception.sql.EntityNotFoundException;
import org.project.model.dto.ServiceDTO;
import org.project.model.response.ServiceResponse;
import org.project.repository.ServiceRepository;
import org.project.repository.impl.custom.ServiceRepositoryCustom;
import org.project.service.ServiceService;
import org.project.utils.PageUtils;
import org.project.utils.specification.PageSpecificationUtils;
import org.project.utils.specification.SpecificationUtils;
import org.project.utils.specification.search.SearchCriteria;
import org.project.utils.specification.sort.SortCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ServiceServiceImpl implements ServiceService {

    private ServiceRepository serviceRepository;
    private ServiceRepositoryCustom serviceRepositoryCustom;
    private ServiceConverter serviceConverter;
    private PageUtils<ServiceEntity> pageUtils;
    private PageSpecificationUtils<ServiceEntity> pageSpecificationUtils;
    private SpecificationUtils<ServiceEntity> specificationUtils;

    @Autowired
    public void setServiceConverter(ServiceConverter serviceConverter) {
        this.serviceConverter = serviceConverter;
    }

    @Autowired
    public void setServiceRepository(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Autowired
    public void setServiceRepositoryCustom(ServiceRepositoryCustom serviceRepositoryCustom) {
        this.serviceRepositoryCustom = serviceRepositoryCustom;
    }

    @Autowired
    public void setPageUtils(PageUtils<ServiceEntity> pageUtils) {
        this.pageUtils = pageUtils;
    }

    @Autowired
    public void setPageSpecificationUtils(PageSpecificationUtils<ServiceEntity> pageSpecificationUtils) {
        this.pageSpecificationUtils = pageSpecificationUtils;
    }

    @Autowired
    public void setSpecificationUtils(SpecificationUtils<ServiceEntity> specificationUtils) {
        this.specificationUtils = specificationUtils;
    }

    @Override
    public Page<ServiceResponse> getServices(int index, int size, ServiceDTO serviceDTO) {
        Pageable pageable = pageUtils.getPageable(index, size);
        List<SearchCriteria> searchCriterias = List.of(
                new SearchCriteria("productEntity.name", ComparisonOperator.CONTAINS, serviceDTO.getProductEntityName(), JoinType.LEFT),
                new SearchCriteria("productEntity.price", ComparisonOperator.GREATER_THAN_OR_EQUAL_TO, serviceDTO.getMinPrice(), JoinType.LEFT),
                new SearchCriteria("productEntity.price", ComparisonOperator.LESS_THAN_OR_EQUAL_TO, serviceDTO.getMaxPrice(), JoinType.LEFT),
                new SearchCriteria("productEntity.reviewEntities", ComparisonOperator.AVG_GREATER_THAN_OR_EQUAL_TO, serviceDTO.getMinStarRating(), JoinType.LEFT)
        );
        List<SortCriteria> sortCriterias = new ArrayList<>();
        switch (Optional.of(serviceDTO).map(ServiceDTO::getSortFieldName).orElse("")) {
            case "productEntity.name", "productEntity.price" ->
                    sortCriterias.add(new SortCriteria(serviceDTO.getSortFieldName(), AggregationFunction.NONE, serviceDTO.getSortDirection(), JoinType.LEFT));
            case "productEntity.reviewEntities.rating" ->
                    sortCriterias.add(new SortCriteria(serviceDTO.getSortFieldName(), AggregationFunction.AVG, serviceDTO.getSortDirection(), JoinType.LEFT));
            case "productEntity.reviewEntities.id" ->
                    sortCriterias.add(new SortCriteria(serviceDTO.getSortFieldName(), AggregationFunction.COUNT, serviceDTO.getSortDirection(), JoinType.LEFT));

        }
        Page<ServiceEntity> serviceEntityPage = pageSpecificationUtils.getPage(
                specificationUtils.reset()
                        .getSpecifications(searchCriterias, sortCriterias),
                pageable,
                ServiceEntity.class,
                true
        );
        pageUtils.validatePage(serviceEntityPage, ServiceEntity.class);
        return serviceEntityPage.map(serviceConverter::toResponse);
    }

    @Override
    public Page<ServiceResponse> getServicesByDepartment(Long departmentId, int index, int size) {
        Pageable pageable = pageUtils.getPageable(index, size);
        Page<ServiceEntity> serviceEntityPage = serviceRepositoryCustom.findAllByProductEntityProductStatusAndDepartmentEntityIdOrderByProductEntityAverageRatingAndProductEntityReviewCount(WebConstant.PRODUCT_STATUS_ACTIVE, departmentId, pageable);
        pageUtils.validatePage(serviceEntityPage, ServiceEntity.class);
        return serviceEntityPage.map(serviceConverter::toResponse);
    }

    @Override
    public Page<ServiceResponse> searchServicesByDepartmentAndKeyword(Long departmentId, String keyword, int index, int size) {
        Pageable pageable = pageUtils.getPageable(index, size);
        Page<ServiceEntity> serviceEntityPage = serviceRepositoryCustom.findAllByProductEntityProductStatusAndProductEntityNameContainingAndDepartmentEntityIdOrderByProductEntityAverageRatingAndProductEntityReviewCount(WebConstant.PRODUCT_STATUS_ACTIVE, keyword, departmentId, pageable);
        pageUtils.validatePage(serviceEntityPage, ServiceEntity.class);
        return serviceEntityPage.map(serviceConverter::toResponse);
    }

    @Override
    public boolean isActiveServiceExist(Long id) {
        return serviceRepository.existsByIdAndProductEntityProductStatus(id, WebConstant.PRODUCT_STATUS_ACTIVE);
    }

    @Override
    public ServiceResponse getActiveService(Long id) {
        return serviceRepository.findByIdAndProductEntityProductStatus(id, WebConstant.PRODUCT_STATUS_ACTIVE)
                .map(serviceConverter::toResponse)
                .orElseThrow(() -> new EntityNotFoundException(ServiceEntity.class, id));
    }
}
