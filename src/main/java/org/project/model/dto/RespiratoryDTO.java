package org.project.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RespiratoryDTO {
    private Long appointmentId;
    @JsonProperty("breathing_pattern")
    private String breathingPattern;
    private String fremitus;
    @JsonProperty("percussion_note")
    private String purcussionNote;
    private String auscultation;
}
