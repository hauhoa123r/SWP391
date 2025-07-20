package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GenitourinaryResponse {
    private Long id;
    private String kidneyArea;
    private String bladder;
    private String genitalInspection;
    private Timestamp recordedAt;
}
