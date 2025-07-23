package org.project.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractServiceDTO {
    protected int page = 0;
    protected int size = 10;
    protected boolean isFormStarted;
}
