package org.project.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.project.enums.ReviewStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Long id;
    private String title;
    private String content;
    private Integer rating;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private ReviewStatus status;
    private String hideReason;
    
    // Product information
    private Long productId;
    private String productName;
    private String productImage;
    private String productType;
    
    // User information
    private Long userId;
    private String username;
    private String userEmail;
    private String userAvatar;
    
    // Images and replies
    private List<String> reviewImages;
    private List<ReviewReplyDTO> replies;
    
    // Additional fields for display
    private boolean hasImages;
    private int replyCount;
} 