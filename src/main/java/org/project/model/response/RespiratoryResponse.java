package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RespiratoryResponse {
    private Long id;
    private String breathingPattern;
    private String fremitus;
    private String percussionNote;
    private String auscultation;
    private Timestamp recordedAt;
}
