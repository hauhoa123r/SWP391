package org.project.converter;

import org.project.config.ModelMapperConfig;
import org.project.entity.*;
import org.project.exception.mapping.ErrorMappingException;
import org.project.model.dto.ServiceDTO;
import org.project.model.response.ServiceResponse;
import org.project.service.ProductAdditionalInfoService;
import org.project.service.ServiceFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ServiceConverter {

    private ModelMapperConfig modelMapperConfig;
    private ServiceFeatureService serviceFeatureService;
    private ProductAdditionalInfoService productAdditionalInfoService;

    @Autowired
    public void setModelMapperConfig(ModelMapperConfig modelMapperConfig) {
        this.modelMapperConfig = modelMapperConfig;
    }

    @Autowired
    public void setServiceFeatureService(ServiceFeatureService serviceFeatureService) {
        this.serviceFeatureService = serviceFeatureService;
    }

    @Autowired
    public void setProductAdditionalInfoService(ProductAdditionalInfoService productAdditionalInfoService) {
        this.productAdditionalInfoService = productAdditionalInfoService;
    }

    public ServiceResponse toResponse(ServiceEntity serviceEntity) {
        Optional<ServiceResponse> serviceResponseOptional = Optional.ofNullable(modelMapperConfig.mapper().map(serviceEntity, ServiceResponse.class));
        return serviceResponseOptional.orElseThrow(() -> new ErrorMappingException(ServiceResponse.class, ServiceEntity.class));
    }

    public ServiceEntity toEntity(ServiceDTO serviceDTO) {
        ServiceEntity serviceEntity = new ServiceEntity();
        ProductEntity productEntity = new ProductEntity();
        productEntity.setName(serviceDTO.getProductEntityName());
        productEntity.setDescription(serviceDTO.getProductEntityDescription());
        productEntity.setPrice(serviceDTO.getProductEntityPrice());
        productEntity.setImageUrl(serviceDTO.getProductEntityImageUrl());
        if (serviceDTO.getProductEntityProductAdditionalInfoEntities() != null) {
            Set<ProductAdditionalInfoEntity> productAdditionalInfoEntities = serviceDTO.getProductEntityProductAdditionalInfoEntities().stream()
                    .map(infoDTO -> productAdditionalInfoService.save(infoDTO))
                    .collect(Collectors.toSet());
            productEntity.setProductAdditionalInfoEntities(productAdditionalInfoEntities);
        }
        serviceEntity.setProductEntity(productEntity);

        DepartmentEntity departmentEntity = new DepartmentEntity();
        departmentEntity.setId(serviceDTO.getDepartmentEntityId());

        serviceEntity.setDepartmentEntity(departmentEntity);
        if (serviceDTO.getServiceFeatureEntities() != null) {
            List<ServiceFeatureEntity> serviceFeatureEntities = serviceDTO.getServiceFeatureEntities().stream()
                    .map(featureDTO -> serviceFeatureService.save(featureDTO))
                    .collect(Collectors.toList());
            serviceEntity.setServiceFeatureEntities(serviceFeatureEntities);
        }
        return serviceEntity;
    }
}
