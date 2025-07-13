package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SampleScheduleResponse {
    private String barCode;
    private Long id;
    private String patientName;
    private String testType;
    private String scheduleTime;
    private String reCollected;
    private String reCollectReason;
}
