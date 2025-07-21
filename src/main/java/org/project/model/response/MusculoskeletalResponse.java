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
public class MusculoskeletalResponse {
    private Long id;
    private String jointExam;
    private String muscleStrength;
    private String deformity;
    private Timestamp recordedAt;
}
