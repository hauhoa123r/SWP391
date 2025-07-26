package org.project.service.impl;

import jakarta.transaction.Transactional;
import org.project.converter.ReviewConverter;
import org.project.entity.ReviewEntity;
import org.project.model.response.ReviewResponse;
import org.project.repository.ReviewRepository;
import org.project.service.ReviewService;
import org.project.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
