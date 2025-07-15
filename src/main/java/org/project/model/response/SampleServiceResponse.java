package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class SampleServiceResponse {
    private String patientName;
    private String testType;
    private String departmentName;
    private String doctorName;
    private long id;
    private Long patientId;
}
