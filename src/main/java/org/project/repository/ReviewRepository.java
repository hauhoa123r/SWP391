package org.project.repository;

import org.project.entity.ReviewEntity;
import org.project.enums.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    
    /**
     * Tìm đánh giá theo trạng thái
     */
    Page<ReviewEntity> findByStatus(ReviewStatus status, Pageable pageable);
    
    /**
     * Tìm đánh giá theo rating
     */
    Page<ReviewEntity> findByRating(Integer rating, Pageable pageable);
    
    /**
     * Tìm đánh giá theo sản phẩm
     */
    List<ReviewEntity> findByProductId(Long productId);
    
    /**
     * Tìm đánh giá theo người dùng
     */
    List<ReviewEntity> findByUserId(Long userId);
    
    /**
     * Tìm kiếm đánh giá với nhiều điều kiện kết hợp
     */
    @Query("SELECT r FROM ReviewEntity r " +
           "WHERE (:search IS NULL OR " +
           "       LOWER(r.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "       LOWER(r.content) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "       LOWER(r.product.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "       LOWER(r.user.username) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:rating IS NULL OR r.rating = :rating) " +
           "AND (:status IS NULL OR r.status = :status)")
    Page<ReviewEntity> findReviews(
            @Param("search") String search,
            @Param("rating") Integer rating,
            @Param("status") ReviewStatus status,
            Pageable pageable);
    
    /**
     * Đếm số lượng đánh giá theo trạng thái
     */
    long countByStatus(ReviewStatus status);
}
