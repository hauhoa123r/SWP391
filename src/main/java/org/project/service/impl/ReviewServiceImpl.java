package org.project.service.impl;

import lombok.RequiredArgsConstructor;
import org.project.entity.ReviewEntity;
import org.project.model.response.ReviewResponse;
import org.project.repository.ReviewRepository;
import org.project.service.ReviewService;
import org.project.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
