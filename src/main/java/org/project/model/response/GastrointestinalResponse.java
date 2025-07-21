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
public class GastrointestinalResponse {
    private Long id;
    private String abdominalInspection;
    private String palpation;
    private String percussion;
    private String auscultation;
    private Timestamp recordedAt;
}
