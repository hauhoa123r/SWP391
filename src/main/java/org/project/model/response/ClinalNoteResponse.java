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
public class ClinalNoteResponse {
    private Long id;
    private String noteText;
    private Timestamp recordedAt;
}
