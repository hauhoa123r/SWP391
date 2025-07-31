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
public class NeurologicDTO {
    private Long appointmentId;
    private String consciousness;
    @JsonProperty("cranial_nerves")
    private String cranialNerves;
    @JsonProperty("motor_function")
    private String motorFunction;
    @JsonProperty("sensory_function")
    private String sensoryFunction;
    private String reflexes;
}
