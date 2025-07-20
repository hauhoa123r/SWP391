package org.project.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AddRespiratoryRequest {
    private String breathingPattern;
    private String fremitus;
    private String percussionNote;
    private String auscultation;
}
