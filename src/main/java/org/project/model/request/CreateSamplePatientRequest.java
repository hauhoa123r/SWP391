package org.project.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateSamplePatientRequest {
    private Long testRequestId;
    private Long sampleId;
    private Long managerId;
    private String date;
    private String time;
    private String note;
}
