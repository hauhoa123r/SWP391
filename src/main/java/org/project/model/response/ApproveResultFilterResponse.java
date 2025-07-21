package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApproveResultFilterResponse extends SampleServiceResponse{
    private String tester;
    private String requestAt;
    private String status;
    private String resultId;
}
