package org.project.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MedicalProfileVResponse {
    Long id;
    List<String> allergies;
    List<String> chronicDiseases;
}
