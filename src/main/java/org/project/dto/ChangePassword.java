package org.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;



public record ChangePassword(
        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,
        @NotBlank(message = "Confirm password is required")
        String confirmPassword
) {}