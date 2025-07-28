package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ViewTestRequestDetailResponse extends SampleServiceResponse{
    private Long testRequestId;
    private String managerName;
    List<SampleResponse> sampleResponses;
}