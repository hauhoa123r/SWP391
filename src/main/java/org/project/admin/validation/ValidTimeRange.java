package org.project.admin.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidTimeRangeValidator.class)
@Documented
public @interface ValidTimeRange {
    String message() default "Giờ kết thúc phải sau giờ bắt đầu và trong cùng ngày";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String startTimeField() default "startTime";
    String endTimeField() default "endTime";
}
