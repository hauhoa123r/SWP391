package org.project.repository.impl.custom;

import org.project.entity.DoctorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DoctorRepositoryCustom {
    Page<DoctorEntity> findAllOrderByStaffEntityAverageRatingAndStaffEntityReviewCount(Pageable pageable);

    Page<DoctorEntity> findAllByStaffEntityDepartmentEntityIdOrderByStaffEntityAverageRatingAndStaffEntityReviewCount(
            Long departmentEntityId, Pageable pageable);

    Page<DoctorEntity> findAllByStaffEntityDepartmentEntityIdAndStaffEntityHospitalEntityIdOrderByStaffEntityAverageRatingAndStaffEntityReviewCount(
            Long departmentEntityId, Long hospitalEntityId, Pageable pageable);

    Page<DoctorEntity> findAllByStaffEntityDepartmentEntityIdAndStaffEntityHospitalEntityIdAndStaffEntityFullNameContainingOrderByStaffEntityAverageRatingAndStaffEntityReviewCount(Long departmentEntityId,
                                                                                                                                                                                    Long hospitalEntityId, String fullName, Pageable pageable);

    List<DoctorEntity> findAllByStaffEntityDepartmentEntityIdAndIdNotEqualsOrderByStaffEntityAverageRatingAndStaffEntityReviewCount(
            Long departmentEntityId, Long id);
}
