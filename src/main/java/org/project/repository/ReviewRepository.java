package org.project.repository;

import org.project.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    Long countByRating(Integer rating);
}
