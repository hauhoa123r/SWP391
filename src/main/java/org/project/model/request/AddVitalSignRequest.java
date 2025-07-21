package org.project.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AddVitalSignRequest {
    private Integer pulseRate;
    private Integer bpSystolic;
    private Integer bpDiastolic;
    private BigDecimal temperature;
    private Integer respiratoryRate;
    private Integer spo2;
}
