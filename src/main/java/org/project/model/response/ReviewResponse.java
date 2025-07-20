package org.project.model.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewResponse {
    private Long id;
    private String reviewerName;
    private String comment;
    private int rating;
    private LocalDateTime reviewDate;
}
