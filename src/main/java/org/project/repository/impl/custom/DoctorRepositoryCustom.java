package org.project.repository.impl.custom;

import java.util.List;

import org.project.entity.DoctorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DoctorRepositoryCustom {
    Page<DoctorEntity> findAllOrderByStaffEntityAverageRatingAndStaffEntityReviewCount(Pageable pageable);

    Page<DoctorEntity> findAllByStaffEntityDepartmentEntityIdOrderByStaffEntityAverageRatingAndStaffEntityReviewCount(
            Long departmentEntityId, Pageable pageable);

    Page<DoctorEntity> findAllByStaffEntityDepartmentEntityIdAndStaffEntityHospitalEntityIdOrderByStaffEntityAverageRatingAndStaffEntityReviewCount(
            Long departmentEntityId, Long hospitalEntityId, Pageable pageable);

    Page<DoctorEntity> findAllByStaffEntityDepartmentEntityIdAndStaffEntityHospitalEntityIdAndStaffEntityFullNameContainingOrderByStaffEntityAverageRatingAndStaffEntityReviewCount(Long departmentEntityId,
                                                                                                                                                                                    Long hospitalEntityId, String keyword, Pageable pageable);

    List<DoctorEntity> findAllByStaffEntityDepartmentEntityIdAndIdNotEqualsOrderByStaffEntityAverageRatingAndStaffEntityReviewCount(
            Long departmentEntityId, Long id);
}
