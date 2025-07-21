package org.project.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AvailabilityRequestDTO {
    private Long staffId;
    private Timestamp startTime;
    private Timestamp endTime;
}
