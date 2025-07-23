package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SampleScheduleResponse extends SampleServiceResponse {
    private String scheduleTime;
    private String reCollected;
    private String reCollectReason;
}
