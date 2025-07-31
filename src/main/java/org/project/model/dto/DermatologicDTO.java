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
public class DermatologicDTO {
    private Long appointmentId;
    @JsonProperty("skin_appearance")
    private String skinAppearance;
    private String rash;
    private String lesions;
}
