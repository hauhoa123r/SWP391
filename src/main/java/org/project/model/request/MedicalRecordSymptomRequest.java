package org.project.model.request;


import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MedicalRecordSymptomRequest {
    String symptomName;
    String duration;
    String severity;
    String description;
}

