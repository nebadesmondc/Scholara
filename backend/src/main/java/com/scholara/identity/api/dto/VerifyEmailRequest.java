package com.scholara.identity.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

/**
 * Request DTO for email verification.
 */
public record VerifyEmailRequest(
        @NotNull(message = "User ID is required")
        UUID userId,

        @NotBlank(message = "OTP code is required")
        @Pattern(regexp = "^\\d{6}$", message = "OTP must be a 6-digit code")
        String otp
) {
}
