package org.project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.enums.ReviewStatus;
import org.project.model.dto.ReviewDTO;
import org.project.model.dto.ReviewReplyDTO;
import org.project.model.response.ReviewResponse;
import org.project.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    
    /**
     * Hiển thị danh sách đánh giá với phân trang và lọc
     */
    @GetMapping("/product-review")
    public String getReviewList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) ReviewStatus status,
            @RequestParam(required = false, defaultValue = "createdDate") String sort,
            @RequestParam(required = false, defaultValue = "desc") String direction,
            Model model) {
        
        log.info("Fetching reviews with page={}, size={}, search={}, rating={}, status={}, sort={}, direction={}", 
                page, size, search, rating, status, sort, direction);
        
        Page<ReviewResponse> reviewPage = reviewService.findReviews(page, size, search, rating, status, sort, direction);
        
        model.addAttribute("reviews", reviewPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", reviewPage.getTotalPages());
        model.addAttribute("totalItems", reviewPage.getTotalElements());
        
        return "templates_storage/product-review";
    }
    
    /**
     * Hiển thị chi tiết một đánh giá
     */
    @GetMapping("/review-detail/{id}")
    public String getReviewDetail(@PathVariable Long id, Model model) {
        log.info("Getting review detail for ID: {}", id);
        
        ReviewDTO review = reviewService.findReviewById(id);
        if (review == null) {
            return "redirect:/product-review";
        }
        
        model.addAttribute("review", review);
        return "templates_storage/review-detail";
    }
    
    /**
     * Xử lý duyệt đánh giá
     */
    @PostMapping("/approve-review/{id}")
    public String approveReview(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.info("Approving review with ID: {}", id);
        
        try {
            reviewService.updateReviewStatus(id, ReviewStatus.APPROVED, null);
            redirectAttributes.addFlashAttribute("message", "Đánh giá đã được duyệt thành công");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            log.error("Error approving review: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("message", "Không thể duyệt đánh giá: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "danger");
        }
        
        return "redirect:/product-review";
    }
    
    /**
     * Xử lý ẩn đánh giá
     */
    @PostMapping("/hide-review/{id}")
    public String hideReview(
            @PathVariable Long id,
            @RequestParam(required = false) String hideReason,
            RedirectAttributes redirectAttributes) {
        
        log.info("Hiding review with ID: {}, reason: {}", id, hideReason);
        
        try {
            reviewService.updateReviewStatus(id, ReviewStatus.HIDDEN, hideReason);
            redirectAttributes.addFlashAttribute("message", "Đánh giá đã được ẩn thành công");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            log.error("Error hiding review: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("message", "Không thể ẩn đánh giá: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "danger");
        }
        
        return "redirect:/product-review";
    }
    
    /**
     * Xử lý thêm phản hồi cho đánh giá
     */
    @PostMapping("/reply-review/{id}")
    public String addReviewReply(
            @PathVariable Long id,
            @RequestParam String replyContent,
            RedirectAttributes redirectAttributes) {
        
        log.info("Adding reply to review ID: {}", id);
        
        try {
            ReviewReplyDTO replyDTO = new ReviewReplyDTO();
            replyDTO.setContent(replyContent);
            replyDTO.setReviewId(id);
            
            reviewService.addReviewReply(replyDTO);
            
            redirectAttributes.addFlashAttribute("message", "Phản hồi đã được thêm thành công");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            log.error("Error adding reply: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("message", "Không thể thêm phản hồi: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "danger");
        }
        
        return "redirect:/review-detail/" + id;
    }
}
