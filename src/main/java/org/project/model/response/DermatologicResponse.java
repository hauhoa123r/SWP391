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
public class DermatologicResponse {
    private Long id;
    private String skinAppearance;
    private String rash;
    private String lesions;
    private Timestamp recordedAt;
}
