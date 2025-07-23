package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private String reviewerName;
    private String comment;
    private int rating;
    private LocalDateTime reviewDate;
    private PatientResponse patientEntity;
    private String content;
}
