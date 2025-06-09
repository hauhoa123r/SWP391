package org.project.model.dai;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DataUserDAI {
    private String bloodType;
    private String allergies;
    private String chronicDiseases;
    private String gender;
    private String fullName;
}
