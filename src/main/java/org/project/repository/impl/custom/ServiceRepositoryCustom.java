package org.project.repository.impl.custom;

import org.project.entity.ServiceEntity;
import org.project.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServiceRepositoryCustom {

    Page<ServiceEntity> findAllByProductEntityProductStatusOrderByProductEntityAverageRatingAndProductEntityReviewCount(ProductStatus productEntityProductStatus, Pageable pageable);

    Page<ServiceEntity> findAllByProductEntityProductStatusAndDepartmentEntityIdOrderByProductEntityAverageRatingAndProductEntityReviewCount(ProductStatus productEntityProductStatus, Long departmentEntityId, Pageable pageable);

    Page<ServiceEntity> findAllByProductEntityProductStatusAndProductEntityNameContainingAndDepartmentEntityIdOrderByProductEntityAverageRatingAndProductEntityReviewCount(ProductStatus productEntityProductStatus, String productEntityName, Long departmentEntityId, Pageable pageable);
}
