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
public class CardiacDTO {
    private Long appointmentId;

    @JsonProperty("heart_rate")
    private int heartRate;

    @JsonProperty("heart_sounds")
    private String heartSound;

    private String murmur;

    @JsonProperty("jugular_venous_pressure")
    private String jugularVenousPressure;

    private String edema;
}
