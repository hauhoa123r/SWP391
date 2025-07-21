package org.project.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TestRequestInAppointment {
    Long id;
    String testTypeEntityTestTypeName;
    LocalDate requestTime;
}
