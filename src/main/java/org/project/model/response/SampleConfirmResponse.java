package org.project.model.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SampleConfirmResponse extends SampleServiceResponse {
    private String sampleTime;
    private String managerName;
    private String note;
}
