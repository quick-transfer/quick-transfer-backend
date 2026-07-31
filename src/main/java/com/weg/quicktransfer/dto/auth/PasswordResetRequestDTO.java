package com.weg.quicktransfer.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PasswordResetRequestDTO(
        @NotBlank(message = "Current password can't be blank")
        String currentPassword,

        @NotBlank(message = "New password can't be blank")
        @Size(min = 14, message = "Password can't have less than 14 characters")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
                message = "The password must contain uppercase, lowercase, number, and special characters"
        )
        String newPassword
) {
}
