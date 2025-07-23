package org.project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.model.response.ReviewResponse;
import org.project.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByProductId(@PathVariable("productId") Long productId) {
        log.info("Fetching reviews for product ID: {}", productId);
        List<ReviewResponse> reviews = reviewService.findReviewsByProductId(productId);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/product/{productId}")
    public ResponseEntity<ReviewResponse> submitReview(
            @PathVariable("productId") Long productId,
            @RequestParam("reviewerName") String reviewerName,
            @RequestParam("comment") String comment,
            @RequestParam("rating") int rating) {
        log.info("Submitting review for product ID: {}", productId);
        ReviewResponse savedReview = reviewService.saveReview(productId, reviewerName, comment, rating);
        return ResponseEntity.ok(savedReview);
    }
}
