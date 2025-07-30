package org.project.service.impl;

import jakarta.persistence.criteria.JoinType;
import jakarta.transaction.Transactional;
import org.project.config.WebConstant;
import org.project.converter.ServiceConverter;
import org.project.entity.*;
import org.project.enums.operation.AggregationFunction;
import org.project.enums.operation.ComparisonOperator;
import org.project.enums.operation.SortDirection;
import org.project.exception.ErrorResponse;
import org.project.exception.sql.EntityNotFoundException;
import org.project.model.dto.ServiceDTO;
import org.project.model.response.ServiceResponse;
import org.project.repository.*;
import org.project.repository.impl.custom.ServiceRepositoryCustom;
import org.project.service.ServiceService;
import org.project.utils.FieldNameUtils;
import org.project.utils.MergeObjectUtils;
import org.project.utils.PageUtils;
import org.project.utils.specification.PageSpecificationUtils;
import org.project.utils.specification.SpecificationUtils;
import org.project.utils.specification.search.SearchCriteria;
import org.project.utils.specification.sort.SortCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional
public class ServiceServiceImpl implements ServiceService {

    private ServiceRepository serviceRepository;
    private DepartmentRepository departmentRepository;
    private ProductRepository productRepository;
    private ProductAdditionalInfoRepository productAdditionalInfoRepository;
    private ServiceFeatureRepository serviceFeatureRepository;
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
    public void setDepartmentRepository(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Autowired
    public void setProductAdditionalInfoRepository(ProductAdditionalInfoRepository productAdditionalInfoRepository) {
        this.productAdditionalInfoRepository = productAdditionalInfoRepository;
    }

    @Autowired
    public void setServiceFeatureRepository(ServiceFeatureRepository serviceFeatureRepository) {
        this.serviceFeatureRepository = serviceFeatureRepository;
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
        List<SearchCriteria> searchCriterias = List.of(new SearchCriteria(FieldNameUtils.joinFields(ServiceEntity.Fields.productEntity, ProductEntity.Fields.name), ComparisonOperator.CONTAINS, serviceDTO.getProductEntityName(), JoinType.LEFT), new SearchCriteria(FieldNameUtils.joinFields(ServiceEntity.Fields.productEntity, ProductEntity.Fields.description), ComparisonOperator.CONTAINS, serviceDTO.getProductEntityDescription(), JoinType.LEFT), new SearchCriteria(FieldNameUtils.joinFields(ServiceEntity.Fields.departmentEntity, DepartmentEntity.Fields.id), ComparisonOperator.EQUALS, serviceDTO.getDepartmentEntityId(), JoinType.LEFT), new SearchCriteria(FieldNameUtils.joinFields(ServiceEntity.Fields.productEntity, ProductEntity.Fields.price), ComparisonOperator.GREATER_THAN_OR_EQUAL_TO, serviceDTO.getMinPrice(), JoinType.LEFT), new SearchCriteria(FieldNameUtils.joinFields(ServiceEntity.Fields.productEntity, ProductEntity.Fields.price), ComparisonOperator.LESS_THAN_OR_EQUAL_TO, serviceDTO.getMaxPrice(), JoinType.LEFT), new SearchCriteria(FieldNameUtils.joinFields(ServiceEntity.Fields.productEntity, ProductEntity.Fields.reviewEntities, ReviewEntity.Fields.rating), ComparisonOperator.AVG_GREATER_THAN_OR_EQUAL_TO, serviceDTO.getMinStarRating(), JoinType.LEFT), new SearchCriteria("productEntity.productStatus", ComparisonOperator.EQUALS, WebConstant.PRODUCT_STATUS_ACTIVE, JoinType.LEFT));
        List<SortCriteria> sortCriterias = new ArrayList<>();
        Map<String, SortCriteria> sortCriteriaMap = Map.of(FieldNameUtils.joinFields(ServiceEntity.Fields.productEntity, ProductEntity.Fields.name), new SortCriteria(serviceDTO.getSortFieldName(), AggregationFunction.NONE, serviceDTO.getSortDirection(), JoinType.LEFT), FieldNameUtils.joinFields(ServiceEntity.Fields.productEntity, ProductEntity.Fields.price), new SortCriteria(serviceDTO.getSortFieldName(), AggregationFunction.NONE, serviceDTO.getSortDirection(), JoinType.LEFT), FieldNameUtils.joinFields(ServiceEntity.Fields.productEntity, ProductEntity.Fields.reviewEntities, ReviewEntity.Fields.rating), new SortCriteria(serviceDTO.getSortFieldName(), AggregationFunction.AVG, serviceDTO.getSortDirection(), JoinType.LEFT), FieldNameUtils.joinFields(ServiceEntity.Fields.productEntity, ProductEntity.Fields.reviewEntities, ReviewEntity.Fields.id), new SortCriteria(serviceDTO.getSortFieldName(), AggregationFunction.COUNT, serviceDTO.getSortDirection(), JoinType.LEFT), FieldNameUtils.joinFields(ServiceEntity.Fields.departmentEntity, DepartmentEntity.Fields.id), new SortCriteria(serviceDTO.getSortFieldName(), AggregationFunction.NONE, serviceDTO.getSortDirection(), JoinType.LEFT));
        if (sortCriteriaMap.containsKey(Optional.of(serviceDTO).map(ServiceDTO::getSortFieldName).orElse(""))) {
            sortCriterias = List.of(sortCriteriaMap.get(serviceDTO.getSortFieldName()));
        }

        Page<ServiceEntity> serviceEntityPage = pageSpecificationUtils.getPage(specificationUtils.reset().getSpecifications(searchCriterias, sortCriterias), pageable, ServiceEntity.class, true);
        pageUtils.validatePage(serviceEntityPage, ServiceEntity.class);
        return serviceEntityPage.map(serviceConverter::toResponse);
    }

    @Override
    public List<ServiceResponse> getTop3ServicesByHospital(Long hospitalId) {
        Pageable pageable = pageUtils.getPageable(0, 3);
        List<SearchCriteria> searchCriterias = List.of(new SearchCriteria(FieldNameUtils.joinFields(ServiceEntity.Fields.departmentEntity, DepartmentEntity.Fields.staffEntities, StaffEntity.Fields.hospitalEntity, HospitalEntity.Fields.id), ComparisonOperator.EQUALS, hospitalId, JoinType.LEFT), new SearchCriteria("productEntity.productStatus", ComparisonOperator.EQUALS, WebConstant.PRODUCT_STATUS_ACTIVE, JoinType.LEFT));
        List<SortCriteria> sortCriterias = List.of(new SortCriteria(FieldNameUtils.joinFields(ServiceEntity.Fields.productEntity, ProductEntity.Fields.reviewEntities, ReviewEntity.Fields.id), AggregationFunction.COUNT, SortDirection.DESC, JoinType.LEFT), new SortCriteria(FieldNameUtils.joinFields(ServiceEntity.Fields.productEntity, ProductEntity.Fields.reviewEntities, ReviewEntity.Fields.rating), AggregationFunction.AVG, SortDirection.DESC, JoinType.LEFT));
        Page<ServiceEntity> serviceEntityPage = pageSpecificationUtils.getPage(specificationUtils.reset().getSpecifications(searchCriterias, sortCriterias), pageable, ServiceEntity.class, true);
        return serviceEntityPage.stream().map(serviceConverter::toResponse).toList();
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
        return serviceRepository.findByIdAndProductEntityProductStatus(id, WebConstant.PRODUCT_STATUS_ACTIVE).map(serviceConverter::toResponse).orElseThrow(() -> new EntityNotFoundException(ServiceEntity.class, id));
    }

    @Override
    public Long countActiveService() {
        return serviceRepository.countByProductEntityProductStatus(WebConstant.PRODUCT_STATUS_ACTIVE);
    }

    @Override
    public List<ServiceResponse> getTopServices(int top) {
        Pageable pageable = pageUtils.getPageable(0, top);
        List<SearchCriteria> searchCriterias = List.of(new SearchCriteria("productEntity.productStatus", ComparisonOperator.EQUALS, WebConstant.PRODUCT_STATUS_ACTIVE, JoinType.LEFT));
        List<SortCriteria> sortCriterias = List.of(new SortCriteria(FieldNameUtils.joinFields(ServiceEntity.Fields.productEntity, ProductEntity.Fields.reviewEntities, ReviewEntity.Fields.id), AggregationFunction.COUNT, SortDirection.DESC, JoinType.LEFT), new SortCriteria(FieldNameUtils.joinFields(ServiceEntity.Fields.productEntity, ProductEntity.Fields.reviewEntities, ReviewEntity.Fields.rating), AggregationFunction.AVG, SortDirection.DESC, JoinType.LEFT));
        Page<ServiceEntity> serviceEntityPage = pageSpecificationUtils.getPage(specificationUtils.reset().getSpecifications(searchCriterias, sortCriterias), pageable, ServiceEntity.class, true);
        return serviceEntityPage.stream().map(serviceConverter::toResponse).toList();
    }

    private void saveService(ServiceEntity serviceEntity) {
        // Save the product

        ProductEntity productEntity = serviceEntity.getProductEntity();
        Set<ProductAdditionalInfoEntity> productAdditionalInfoEntities = productEntity.getProductAdditionalInfoEntities();
        productEntity.setProductAdditionalInfoEntities(null);
        productEntity = productRepository.save(productEntity);

        // Set product to additional info
        if (productAdditionalInfoEntities != null) {
            for (ProductAdditionalInfoEntity additionalInfo : productAdditionalInfoEntities) {
                additionalInfo.setProductEntity(productEntity);
                // Save additional info
                productAdditionalInfoRepository.save(additionalInfo);
            }
        }

        // Save the service
        serviceEntity.setProductEntity(productEntity);
        List<ServiceFeatureEntity> serviceFeatureEntities = serviceEntity.getServiceFeatureEntities();
        serviceEntity.setServiceFeatureEntities(null);
        serviceEntity = serviceRepository.save(serviceEntity);


        // Save service to feature
        // Save feature
        if (serviceFeatureEntities != null) {
            for (ServiceFeatureEntity feature : serviceFeatureEntities) {
                feature.setServiceEntity(serviceEntity);
                serviceFeatureRepository.save(feature);
            }
        }
    }

    private void updateParent(ServiceEntity serviceEntity) {
        ProductEntity productEntity = serviceEntity.getProductEntity();
        productEntity.getProductAdditionalInfoEntities().forEach(productAdditionalInfoEntity -> {
            productAdditionalInfoEntity.setProductEntity(serviceEntity.getProductEntity());
            productAdditionalInfoRepository.save(productAdditionalInfoEntity);
        });

        serviceEntity.getServiceFeatureEntities().forEach(serviceFeatureEntity -> {
            serviceFeatureEntity.setServiceEntity(serviceEntity);
            serviceFeatureRepository.save(serviceFeatureEntity);
        });
    }

    @Override
    public void createService(ServiceDTO serviceDTO) {
        // Validate
        if (!departmentRepository.existsByIdAndDepartmentStatus(serviceDTO.getDepartmentEntityId(), WebConstant.DEPARTMENT_STATUS_ACTIVE)) {
            throw new ErrorResponse("Phòng ban không tồn tại hoặc đã bị xóa");
        }

        ServiceEntity serviceEntity = serviceConverter.toEntity(serviceDTO);
        ProductEntity productEntity = serviceEntity.getProductEntity();
        productEntity.setProductStatus(WebConstant.PRODUCT_STATUS_ACTIVE);
        productEntity.setProductType(WebConstant.PRODUCT_TYPE_SERVICE);
        productEntity.setUnit("Dịch vụ");
        productEntity.setLabel(WebConstant.PRODUCT_STATUS_LABEL_STANDARD);
        serviceEntity.setProductEntity(productEntity);
        serviceEntity = serviceRepository.save(serviceEntity);
        updateParent(serviceEntity);
    }

    @Override
    public void updateService(Long serviceId, ServiceDTO serviceDTO) {
        // Validate
        if (!departmentRepository.existsByIdAndDepartmentStatus(serviceDTO.getDepartmentEntityId(), WebConstant.DEPARTMENT_STATUS_ACTIVE)) {
            throw new ErrorResponse("Phòng ban không tồn tại hoặc đã bị xóa");
        }

        ServiceEntity target = serviceRepository.findByIdAndProductEntityProductStatus(serviceId, WebConstant.PRODUCT_STATUS_ACTIVE).orElseThrow(() -> new EntityNotFoundException(ServiceEntity.class, serviceId));
        ServiceEntity source = serviceConverter.toEntity(serviceDTO);

        MergeObjectUtils.mergeNonNullFields(source, target);

        ProductEntity productEntity = target.getProductEntity();
        productEntity.getProductAdditionalInfoEntities().clear();
        productEntity.getProductAdditionalInfoEntities().addAll(source.getProductEntity().getProductAdditionalInfoEntities());
        target.setProductEntity(productEntity);

        target.getServiceFeatureEntities().clear();
        target.getServiceFeatureEntities().addAll(source.getServiceFeatureEntities());

        ServiceEntity serviceEntity = serviceRepository.save(target);
        updateParent(serviceEntity);
    }

    @Override
    public void deleteService(Long serviceId) {
        ServiceEntity serviceEntity = serviceRepository.findByIdAndProductEntityProductStatus(serviceId, WebConstant.PRODUCT_STATUS_ACTIVE).orElseThrow(() -> new EntityNotFoundException(ServiceEntity.class, serviceId));
        serviceEntity.getProductEntity().setProductStatus(WebConstant.PRODUCT_STATUS_INACTIVE);
        serviceRepository.save(serviceEntity);
    }
}
