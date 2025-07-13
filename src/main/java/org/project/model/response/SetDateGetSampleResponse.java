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
public class SetDateGetSampleResponse
{
    private Long testRequestId;
    private Long sampleId;
    private String patientName;
    private String testType;
    private String departmentName;
    private String hospitalName;
    private List<ManagerNameResponse> managerName;
}
