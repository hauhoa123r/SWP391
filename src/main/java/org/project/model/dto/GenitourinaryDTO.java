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
public class GenitourinaryDTO {
    private Long appointmentId;
    @JsonProperty("kidney_area")
    private String kidneyArea;
    private String bladder;
    @JsonProperty("genital_inspection")
    private String genitalInspection;

}
