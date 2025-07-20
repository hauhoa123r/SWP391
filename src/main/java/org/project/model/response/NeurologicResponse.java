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
public class NeurologicResponse {
    private Long id;
    private String consciousness;
    private String cranialNerves;
    private String motorFunction;
    private String sensoryFunction;
    private String reflexes;
    private Timestamp recordedAt;
}
