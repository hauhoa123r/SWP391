package org.project.service.impl;

import org.project.converter.ReviewConverter;
import org.project.entity.ReviewEntity;
import org.project.enums.ReviewStatus;
import org.project.model.dto.ReviewDTO;
import org.project.model.dto.ReviewReplyDTO;
import org.project.model.response.ReviewResponse;
import org.project.model.response.ReviewSpecification;
import org.project.repository.ReviewRepository;
import org.project.service.ReviewService;
import org.project.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {
    private ReviewRepository reviewRepository;
    private ReviewConverter reviewConverter;
    private PageUtils<ReviewEntity> pageUtils;

    @Autowired
    public void setReviewRepository(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Autowired
    public void setReviewConverter(ReviewConverter reviewConverter) {
        this.reviewConverter = reviewConverter;
    }

    @Autowired
    public void setPageUtils(PageUtils<ReviewEntity> pageUtils) {
        this.pageUtils = pageUtils;
    }

    @Override
    public Long count5StarReviews() {
        return reviewRepository.countByRating(5);
    }

    @Override
    public List<ReviewResponse> getTop5Reviews() {
        Sort sort = Sort.by(Sort.Direction.DESC, "rating");
        Pageable pageable = pageUtils.getPageable(0, 5, sort);
        return reviewRepository.findAll(pageable).map(reviewConverter::toResponse).getContent();
    }

    @Override
    public Page<ReviewResponse> findReviews(int page, int size, String search, Integer rating, ReviewStatus status, String sort, String direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort));
        Specification<ReviewEntity> spec = ReviewSpecification.filter(search, rating, status);
        Page<ReviewEntity> reviewPage = reviewRepository.findAll(spec, pageable);
        return reviewPage.map(reviewConverter::toResponse);
    }

    @Override
    public ReviewDTO findReviewById(Long id) {
        return null;
    }

    @Override
    public void updateReviewStatus(Long id, ReviewStatus status, String hideReason) {

    }

    @Override
    public ReviewReplyDTO addReviewReply(ReviewReplyDTO replyDTO) {
        return null;
    }

    @Override
    public List<ReviewDTO> findReviewsByProduct(Long productId) {
        return List.of();
    }

    @Override
    public List<ReviewDTO> findReviewsByUser(Long userId) {
        return List.of();
    }

    @Override
    public long countReviewsByStatus(ReviewStatus status) {
        return 0;
    }

    @Override
    public List<ReviewResponse> findReviewsByProductId(Long productId) {
        List<ReviewEntity> reviews = reviewRepository.findByProductId(productId);
        return reviews.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewResponse saveReview(Long productId, String reviewerName, String comment, int rating) {
        ReviewEntity review = new ReviewEntity();

        review.setContent(comment);
        review.setRating(rating);

        ReviewEntity savedReview = reviewRepository.save(review);
        return convertToDto(savedReview);
    }

    private ReviewResponse convertToDto(ReviewEntity entity) {
        ReviewResponse dto = new ReviewResponse();
        dto.setId(entity.getId());
        dto.setReviewerName("Anonymous");
        dto.setComment(entity.getContent());
        dto.setRating(entity.getRating());
        dto.setReviewDate(LocalDateTime.now());
        return dto;
    }
}