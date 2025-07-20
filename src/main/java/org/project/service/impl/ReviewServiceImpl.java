package org.project.service.impl;

import lombok.RequiredArgsConstructor;
import org.project.entity.ReviewEntity;
import org.project.model.response.ReviewResponse;
import org.project.repository.ReviewRepository;
import org.project.service.ReviewService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

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
