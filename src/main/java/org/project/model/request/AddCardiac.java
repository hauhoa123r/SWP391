package org.project.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AddCardiac {
    private Integer heartRate;
    private String heartSounds;
    private String murmur;
    private String jugularVenousPressure;
    private String edema;
}
