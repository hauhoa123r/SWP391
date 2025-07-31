package org.project.model.request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FilterVAppointmentRequest {
    private String search;
    String status;
    String dateFilter;
    LocalDate startDate;
    LocalDate endDate;
    @Pattern(regexp = "asc|desc", message = "Sort order must be 'asc' or 'desc'")
    String sortTime;
}
