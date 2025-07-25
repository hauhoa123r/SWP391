package org.project.repository;

import org.project.entity.ReviewReplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewReplyRepository extends JpaRepository<ReviewReplyEntity, Long> {
    
    /**
     * Tìm danh sách phản hồi theo đánh giá
     */
    List<ReviewReplyEntity> findByReviewIdOrderByCreatedDateAsc(Long reviewId);
    
    /**
     * Tìm danh sách phản hồi theo người dùng
     */
    List<ReviewReplyEntity> findByUserId(Long userId);
    
    /**
     * Đếm số lượng phản hồi theo đánh giá
     */
    long countByReviewId(Long reviewId);
} 