package org.project.repository;

import org.project.entity.DoctorEntity;
import org.project.model.response.DoctorResponse;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DoctorRepository extends JpaRepository<DoctorEntity, Long>, JpaSpecificationExecutor<DoctorEntity> {
    List<DoctorResponse> getTopByStaffEntityDepartmentEntityId(Long staffEntityDepartmentEntityId, Limit limit);

    @Query("""
                SELECT d
                FROM DoctorEntity d
                LEFT JOIN d.staffEntity s
                LEFT JOIN s.reviewEntities r
                GROUP BY d
                ORDER BY AVG(r.rating) DESC, COUNT(r.id) DESC
            """)
    Page<DoctorEntity> findTopOrderByStaffEntityAverageRatingDescAndStaffEntityReviewCountDesc(Pageable pageable);
}
