package org.project.repository;

import org.project.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    @Query("SELECT r FROM ReviewEntity r JOIN r.productEntities p WHERE p.id = :productId")
    List<ReviewEntity> findByProductId(@Param("productId") Long productId);
    Long countByRating(Integer rating);
}


