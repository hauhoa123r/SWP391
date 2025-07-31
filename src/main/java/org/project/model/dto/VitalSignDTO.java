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
public class VitalSignDTO {
    private Long appointmentId;
    @JsonProperty("pulse_rate")
    private int pulseRate;
    @JsonProperty("bp_systolic")
    private int bpSystolic;
    @JsonProperty("bp_diastolic")
    private int bpDiastolic;
    private Float temperature;
    @JsonProperty("respiratory_rate")
    private int respiratoryRate;
    private int spo2;
}
