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
public class SetResultResponse extends SampleServiceResponse{
    private Long testTypeId;
    private String managerName;
    private String resultSender;
    private String requestAt;
    private List<String> unitName;
    private String testTypeName;
    private List<String> testItemName;
}
