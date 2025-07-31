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
public class MusculoskeletalDTO {
    private Long appointmentId;
    @JsonProperty("joint_exam")
    private String jointExam;
    @JsonProperty("muscle_strength")
    private String muscleStrength;
    private String deformity;
}
