package org.project.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AddDermatologicRequest {
    private String skinAppearance;
    private String rash;
    private String lesions;
}
