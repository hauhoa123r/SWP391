package org.project.service;

import org.project.model.response.ReviewResponse;

import java.util.List;

public interface ReviewService {
    Long count5StarReviews();

    List<ReviewResponse> getTop5Reviews();
}
