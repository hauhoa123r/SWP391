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
public class GastrointestinalDTO {
    private Long appointmentId;
    @JsonProperty("abdominal_inspection")
    private String abdominalInspection;
    private String palpation;
    private String percussion;
    private String auscultation;
}
