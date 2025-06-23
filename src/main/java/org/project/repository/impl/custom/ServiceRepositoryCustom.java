package org.project.repository.impl.custom;

import org.project.entity.ServiceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServiceRepositoryCustom {

    Page<ServiceEntity> findAllOrderByProductEntityAverageRatingAndProductEntityReviewCount(Pageable pageable);

    Page<ServiceEntity> findAllByDepartmentEntityIdOrderByProductEntityAverageRatingAndProductEntityReviewCount(Long departmentEntityId, Pageable pageable);

    Page<ServiceEntity> findAllByDepartmentEntityIdAndNameContainingOrderByProductEntityAverageRatingAndProductEntityReviewCount(Long departmentEntityId, String keyword, Pageable pageable);
}
