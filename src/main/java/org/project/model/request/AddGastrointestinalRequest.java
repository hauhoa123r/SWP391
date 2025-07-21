package org.project.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AddGastrointestinalRequest {
    private String abdominalInspection;
    private String palpation;
    private String percussion;
    private String auscultation;
}
