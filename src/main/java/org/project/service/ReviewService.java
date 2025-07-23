package org.project.service;

import org.project.enums.ReviewStatus;
import org.project.model.dto.ReviewDTO;
import org.project.model.dto.ReviewReplyDTO;
import org.project.model.response.ReviewResponse;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Service interface cho quản lý đánh giá sản phẩm
 */
public interface ReviewService {
    
    /**
     * Tìm danh sách đánh giá với nhiều điều kiện lọc
     */
    Page<ReviewDTO> findReviews(int page, int size, String search, Integer rating, 
                               ReviewStatus status, String sort, String direction);
    
    /**
     * Tìm đánh giá theo ID
     */
    ReviewDTO findReviewById(Long id);
    
    /**
     * Cập nhật trạng thái đánh giá
     */
    void updateReviewStatus(Long id, ReviewStatus status, String hideReason);
    
    /**
     * Thêm phản hồi cho đánh giá
     */
    ReviewReplyDTO addReviewReply(ReviewReplyDTO replyDTO);
    
    /**
     * Tìm danh sách đánh giá theo sản phẩm
     */
    List<ReviewDTO> findReviewsByProduct(Long productId);
    
    /**
     * Tìm danh sách đánh giá theo người dùng
     */
    List<ReviewDTO> findReviewsByUser(Long userId);
    
    /**
     * Đếm số lượng đánh giá theo trạng thái
     */
    long countReviewsByStatus(ReviewStatus status);
    
    /**
     * Tìm đánh giá theo ID sản phẩm - API cũ
     */
    List<ReviewResponse> findReviewsByProductId(Long productId);
    
    /**
     * Lưu đánh giá mới - API cũ
     */
    ReviewResponse saveReview(Long productId, String reviewerName, String comment, int rating);
}
