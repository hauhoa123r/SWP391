package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class VitalSignResponse {
    private Long id;
    private Integer pulseRate;
    private Integer bpSystolic;
    private Integer bpDiastolic;
    private BigDecimal temperature;
    private Integer respiratoryRate;
    private Integer spo2;
    private Timestamp recordedAt;
}
