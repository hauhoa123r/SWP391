package org.project.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MedicalRecordSymptomResponse {
    Long id;
    String symptomName;
    LocalDate onsetDate;
    String duration;
    String severity;
    String description;
}
