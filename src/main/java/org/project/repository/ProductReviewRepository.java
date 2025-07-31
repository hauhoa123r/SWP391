package org.project.repository;

import org.project.entity.ReviewEntity;
import org.project.model.dto.ReviewDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductReviewRepository extends JpaRepository<ReviewEntity, Long> {
    @Query("SELECT new org.project.model.dto.ReviewDTO"
            + "(p.fullName, p.avatarUrl, r.content, r.rating) "
            + "FROM ProductReviewEntity pr "
            + "JOIN pr.reviewEntity r "
            + "JOIN r.patientEntity p "
            + "WHERE pr.productEntity.id = :productId")
    List<ReviewDTO> findReviewsByProductId(@Param("productId") Long productId);
}
