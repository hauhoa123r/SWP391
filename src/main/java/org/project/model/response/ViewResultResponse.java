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
public class ViewResultResponse {
    private String patientName;
    private String doctorName;
    private String department;
    private String testName;
    List<ViewResultDetailResponse> viewResultDetailResponses;
}
