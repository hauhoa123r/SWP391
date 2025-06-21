package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StaffScheduleResponse {
    private Long id;
    private Date availableDate;
    private Timestamp startTime;
    private Timestamp endTime;
}
