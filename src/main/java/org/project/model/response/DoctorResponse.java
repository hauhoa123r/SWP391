package org.project.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DoctorResponse {
    Long id;
    String staffEntityFullName;
    String staffEntityAvatarUrl;
}
