package org.project.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AddNeurologicRequest {
    private String consciousness;
    private String cranialNerves;
    private String motorFunction;
    private String sensoryFunction;
    private String reflexes;
}
