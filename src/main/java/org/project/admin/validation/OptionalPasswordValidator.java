package org.project.admin.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class OptionalPasswordValidator implements ConstraintValidator<OptionalPassword, String> {

    private int minLength;

    @Override
    public void initialize(OptionalPassword constraintAnnotation) {
        this.minLength = constraintAnnotation.minLength();
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.trim().isEmpty()) {
            return true;
        }

        return password.trim().length() >= minLength;
    }
}
