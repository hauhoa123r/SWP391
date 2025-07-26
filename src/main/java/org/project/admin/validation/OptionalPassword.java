package org.project.admin.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OptionalPasswordValidator.class)
@Documented
public @interface OptionalPassword {
    String message() default "Mật khẩu phải có ít nhất 6 ký tự nếu được nhập";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    int minLength() default 6;
}
