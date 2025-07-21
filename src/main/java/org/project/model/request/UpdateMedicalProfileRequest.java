package org.project.model.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateMedicalProfileRequest {
    List<String> allergies;
    List<String> chronicDiseases;
}
