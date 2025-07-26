package org.project.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewReplyDTO {
    private Long id;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    
    // User information
    private Long userId;
    private String username;
    private String userEmail;
    private String userAvatar;
    
    // Review information
    private Long reviewId;
} 