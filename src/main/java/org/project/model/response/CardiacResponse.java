package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CardiacResponse {
    private Long id;
    private Integer heartRate;
    private String heartSounds;
    private String murmur;
    private String jugularVenousPressure;
    private String edema;
    private Timestamp recordedAt;
}
