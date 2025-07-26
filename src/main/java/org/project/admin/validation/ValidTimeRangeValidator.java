package org.project.admin.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.project.admin.dto.request.StaffScheduleRequest;

import java.time.LocalDateTime;

public class ValidTimeRangeValidator implements ConstraintValidator<ValidTimeRange, StaffScheduleRequest> {

    @Override
    public void initialize(ValidTimeRange constraintAnnotation) {
        // Initialization if needed
    }

    @Override
    public boolean isValid(StaffScheduleRequest request, ConstraintValidatorContext context) {
        if (request == null || request.getStartTime() == null || request.getEndTime() == null) {
            return true; // Let @NotNull handle null validation
        }

        LocalDateTime startTime = request.getStartTime();
        LocalDateTime endTime = request.getEndTime();

        // Kiểm tra giờ kết thúc phải sau giờ bắt đầu
        if (!endTime.isAfter(startTime)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Giờ kết thúc phải sau giờ bắt đầu")
                   .addConstraintViolation();
            return false;
        }

        // Kiểm tra thời gian làm việc hợp lý (không quá 12 tiếng)
        if (startTime.plusHours(12).isBefore(endTime)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Thời gian làm việc không được vượt quá 12 tiếng")
                   .addConstraintViolation();
            return false;
        }

        // Kiểm tra trong cùng ngày
        if (!startTime.toLocalDate().equals(endTime.toLocalDate())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Giờ bắt đầu và kết thúc phải trong cùng một ngày")
                   .addConstraintViolation();
            return false;
        }

        return true;
    }
}
