package org.project.service.impl;

import jakarta.transaction.Transactional;
import org.project.converter.ServiceConverter;
import org.project.entity.ServiceEntity;
import org.project.exception.sql.EntityNotFoundException;
import org.project.model.response.ServiceResponse;
import org.project.repository.ServiceRepository;
import org.project.repository.impl.custom.ServiceRepositoryCustom;
import org.project.service.ServiceService;
import org.project.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ServiceServiceImpl implements ServiceService {

    private ServiceRepository serviceRepository;
    private ServiceRepositoryCustom serviceRepositoryCustom;
    private ServiceConverter serviceConverter;
    private PageUtils<ServiceEntity> pageUtils;

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

    @Override
    public Page<ServiceResponse> getServices(int index, int size) {
        Pageable pageable = pageUtils.getPageable(index, size);
        Page<ServiceEntity> serviceEntityPage = serviceRepositoryCustom.findAllOrderByProductEntityAverageRatingAndProductEntityReviewCount(pageable);
        pageUtils.validatePage(serviceEntityPage, ServiceEntity.class);
        return serviceEntityPage.map(serviceConverter::toResponse);
    }

    @Override
    public Page<ServiceResponse> getServicesByDepartment(Long departmentId, int index, int size) {
        Pageable pageable = pageUtils.getPageable(index, size);
        Page<ServiceEntity> serviceEntityPage = serviceRepositoryCustom.findAllByDepartmentEntityIdOrderByProductEntityAverageRatingAndProductEntityReviewCount(departmentId, pageable);
        pageUtils.validatePage(serviceEntityPage, ServiceEntity.class);
        return serviceEntityPage.map(serviceConverter::toResponse);
    }

    @Override
    public Page<ServiceResponse> searchServicesByDepartmentAndKeyword(Long departmentId, String keyword, int index, int size) {
        Pageable pageable = pageUtils.getPageable(index, size);
        Page<ServiceEntity> serviceEntityPage = serviceRepositoryCustom.findAllByDepartmentEntityIdAndNameContainingOrderByProductEntityAverageRatingAndProductEntityReviewCount(departmentId, keyword, pageable);
        pageUtils.validatePage(serviceEntityPage, ServiceEntity.class);
        return serviceEntityPage.map(serviceConverter::toResponse);
    }

    @Override
    public boolean isServiceExist(Long id) {
        return serviceRepository.existsById(id);
    }

    @Override
    public ServiceResponse getService(Long id) {
        return serviceRepository.findById(id)
                .map(serviceConverter::toResponse)
                .orElseThrow(() -> new EntityNotFoundException(ServiceEntity.class, id));
    }
}
