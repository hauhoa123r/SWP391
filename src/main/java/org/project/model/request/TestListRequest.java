package org.project.model.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.project.enums.RequestStatus;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TestListRequest {
    Long appointmentId;
    List<Long> testTypeId;
}
