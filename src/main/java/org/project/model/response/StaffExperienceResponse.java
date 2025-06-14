package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StaffExperienceResponse {
    private Long id;
    private Integer year;
    private String department;
    private String position;
    private String hospital;
    private String result;
}
