package org.project.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AddMusculoskeletalRequest {
    private String jointExam;
    private String muscleStrength;
    private String deformity;
}
