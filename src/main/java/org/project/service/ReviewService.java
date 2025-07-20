package org.project.service;

import org.project.model.response.ReviewResponse;
import java.util.List;

public interface ReviewService {
    List<ReviewResponse> findReviewsByProductId(Long productId);
    ReviewResponse saveReview(Long productId, String reviewerName, String comment, int rating);
}
