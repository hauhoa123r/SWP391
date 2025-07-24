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
    private Long id;
    private String patientName;
    private String doctorName;
    private String department;
    private String testName;
    private String imagePatient;
    private String note;
    private String dateRequest;
    List<ViewResultDetailResponse> viewResultDetailResponses;
}
