package org.project.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.entity.PatientEntity;
import org.project.entity.ProductEntity;
import org.project.entity.ReviewEntity;
import org.project.entity.ReviewReplyEntity;
import org.project.enums.ReviewStatus;
import org.project.enums.ReviewType;
import org.project.model.dto.ReviewDTO;
import org.project.model.dto.ReviewReplyDTO;
import org.project.model.response.ReviewResponse;
import org.project.repository.PatientRepository;
import org.project.repository.ProductRepository;
import org.project.repository.ReviewReplyRepository;
import org.project.repository.ReviewRepository;
import org.project.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewReplyRepository replyRepository;
    private final ProductRepository productRepository;
    private final PatientRepository patientRepository;
    
    @Override
    public Page<ReviewDTO> findReviews(int page, int size, String search, Integer rating, 
                                      ReviewStatus status, String sort, String direction) {
        log.info("Finding reviews with search: {}, rating: {}, status: {}", search, rating, status);
        
        Sort.Direction sortDirection = Sort.Direction.DESC;
        if (direction != null && direction.equalsIgnoreCase("asc")) {
            sortDirection = Sort.Direction.ASC;
        }
        
        String sortBy = "id"; // Sử dụng id thay vì createdDate vì ReviewEntity không có createdDate
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        // Sử dụng findAll thay vì custom query vì ReviewEntity hiện tại không có các trường cần thiết
        List<ReviewEntity> reviews = reviewRepository.findAll();
        
        // Áp dụng lọc thủ công nếu cần
        if (rating != null) {
            reviews = reviews.stream()
                    .filter(r -> r.getRating().equals(rating))
                    .collect(Collectors.toList());
        }
        
        // Convert tất cả sang DTO
        List<ReviewDTO> reviewDTOs = reviews.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        // Phân trang thủ công
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), reviewDTOs.size());
        
        List<ReviewDTO> pageContent = start < reviewDTOs.size() ? 
                reviewDTOs.subList(start, end) : new ArrayList<>();
                
        return new PageImpl<>(pageContent, pageable, reviewDTOs.size());
    }
    
    @Override
    public ReviewDTO findReviewById(Long id) {
        log.info("Finding review by ID: {}", id);
        
        return reviewRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }
    
    @Override
    @Transactional
    public void updateReviewStatus(Long id, ReviewStatus status, String hideReason) {
        log.info("Updating review status is not supported for current ReviewEntity structure");
        throw new UnsupportedOperationException("Current ReviewEntity does not support status updates");
    }
    
    @Override
    @Transactional
    public ReviewReplyDTO addReviewReply(ReviewReplyDTO replyDTO) {
        log.info("Adding reply to review ID: {}", replyDTO.getReviewId());
        
        ReviewEntity review = reviewRepository.findById(replyDTO.getReviewId())
                .orElseThrow(() -> new IllegalArgumentException("Review not found with ID: " + replyDTO.getReviewId()));
        
        // Tạo và lưu reply
        ReviewReplyEntity reply = new ReviewReplyEntity();
        reply.setContent(replyDTO.getContent());
        reply.setCreatedDate(LocalDateTime.now());
        reply.setReview(review);
        
        ReviewReplyEntity savedReply = replyRepository.save(reply);
        
        return convertToReplyDTO(savedReply);
    }
    
    @Override
    public List<ReviewDTO> findReviewsByProduct(Long productId) {
        log.info("Finding reviews by product ID: {}", productId);
        
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));
        
        // Lấy danh sách reviews từ product
        List<ReviewEntity> reviews = new ArrayList<>(product.getReviewEntities());
        
        return reviews.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ReviewDTO> findReviewsByUser(Long userId) {
        log.info("Finding reviews by user ID is not directly supported with current ReviewEntity");
        
        // Trả về danh sách rỗng vì ReviewEntity hiện tại không liên kết với User mà liên kết với Patient
        return Collections.emptyList();
    }
    
    @Override
    public long countReviewsByStatus(ReviewStatus status) {
        // ReviewEntity hiện tại không có trường status
        log.info("Counting reviews by status is not supported for current ReviewEntity structure");
        return 0;
    }
    
    /**
     * Convert ReviewEntity hiện tại sang ReviewDTO mới
     */
    private ReviewDTO convertToDTO(ReviewEntity entity) {
        if (entity == null) {
            return null;
        }
        
        ReviewDTO dto = new ReviewDTO();
        dto.setId(entity.getId());
        dto.setContent(entity.getContent());
        dto.setRating(entity.getRating());
        
        // Các trường không có trong ReviewEntity hiện tại
        dto.setTitle(""); // Không có trong entity hiện tại
        dto.setStatus(ReviewStatus.APPROVED); // Mặc định là APPROVED vì không có status
        
        // Lấy thông tin product từ ManyToMany relationship
        if (entity.getProductEntities() != null && !entity.getProductEntities().isEmpty()) {
            ProductEntity product = entity.getProductEntities().iterator().next();
            dto.setProductId(product.getId());
            dto.setProductName(product.getName());
            dto.setProductImage(product.getImageUrl());
            dto.setProductType(product.getProductType() != null ? product.getProductType().name() : null);
        }
        
        // Lấy thông tin user từ patient
        if (entity.getPatientEntity() != null) {
            dto.setUserId(entity.getPatientEntity().getId());
            dto.setUsername(entity.getPatientEntity().getFullName());
            dto.setUserEmail(""); // Không có trường email trong PatientEntity
        }
        
        // ReviewEntity hiện tại không có danh sách hình ảnh
        dto.setReviewImages(Collections.emptyList());
        dto.setHasImages(false);
        
        // Lấy danh sách replies từ repository nếu có
        List<ReviewReplyEntity> replies = replyRepository.findByReviewIdOrderByCreatedDateAsc(entity.getId());
        if (replies != null && !replies.isEmpty()) {
            dto.setReplies(replies.stream()
                    .map(this::convertToReplyDTO)
                    .collect(Collectors.toList()));
            dto.setReplyCount(replies.size());
        } else {
            dto.setReplies(Collections.emptyList());
            dto.setReplyCount(0);
        }
        
        return dto;
    }
    
    /**
     * Convert ReviewReplyEntity sang ReviewReplyDTO
     */
    private ReviewReplyDTO convertToReplyDTO(ReviewReplyEntity entity) {
        if (entity == null) {
            return null;
        }
        
        ReviewReplyDTO dto = new ReviewReplyDTO();
        dto.setId(entity.getId());
        dto.setContent(entity.getContent());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setUpdatedDate(entity.getUpdatedDate());
        
        if (entity.getReview() != null) {
            dto.setReviewId(entity.getReview().getId());
        }
        
        // User information - fill in as much as possible from available data
        if (entity.getUser() != null) {
            dto.setUsername(entity.getUser().getEmail());
            dto.setUserId(entity.getUser().getId());
        } else {
            dto.setUsername("Admin"); // Default value since we don't have user info
        }
        
        return dto;
    }
    
    // Implement các phương thức cũ từ interface ReviewService
    @Override
    public List<ReviewResponse> findReviewsByProductId(Long productId) {
        log.info("Finding review responses by product ID: {}", productId);
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));
        
        List<ReviewEntity> reviews = new ArrayList<>(product.getReviewEntities());
        
        return reviews.stream()
                .map(review -> {
                    ReviewResponse response = new ReviewResponse();
                    response.setId(review.getId());
                    response.setComment(review.getContent()); // Sử dụng setComment thay vì setContent
                    response.setRating(review.getRating());
                    if (review.getPatientEntity() != null) {
                        response.setReviewerName(review.getPatientEntity().getFullName());
                    }
                    // Không có createdDate trong ReviewEntity hiện tại
                    return response;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public ReviewResponse saveReview(Long productId, String reviewerName, String comment, int rating) {
        log.info("Saving new review for product ID: {}", productId);
        
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));
        
        // Tìm patient dựa vào reviewerName
        // PatientRepository không có phương thức findByFullName nên sử dụng phương thức khác
        PatientEntity patient = patientRepository.findByUserEntity_IdAndFullName(1L, reviewerName); // User ID 1 là giá trị mặc định
        if (patient == null) {
            log.error("Patient not found with name: {}", reviewerName);
            throw new IllegalArgumentException("Patient not found with name: " + reviewerName);
        }
        
        ReviewEntity review = new ReviewEntity();
        review.setContent(comment);
        review.setRating(rating);
        review.setPatientEntity(patient);
        review.setReviewType(ReviewType.PRODUCT); // Đặt loại đánh giá là PRODUCT
        
        // Thêm review vào danh sách reviews của product
        ReviewEntity savedReview = reviewRepository.save(review);
        product.getReviewEntities().add(savedReview);
        productRepository.save(product);
        
        ReviewResponse response = new ReviewResponse();
        response.setId(savedReview.getId());
        response.setComment(savedReview.getContent()); // Sử dụng setComment thay vì setContent
        response.setRating(savedReview.getRating());
        response.setReviewerName(patient.getFullName());
        
        return response;
    }
}
