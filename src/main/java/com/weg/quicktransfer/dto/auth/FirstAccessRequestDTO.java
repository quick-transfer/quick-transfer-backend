package com.weg.quicktransfer.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FirstAccessRequestDTO(
        @NotBlank(message = "Username can't be blank")
        String username,

        @NotBlank(message = "Current password can't be blank")
        String currentPassword,

        @NotBlank(message = "New password can't be blank")
        @Size(min = 14, message = "Password can't have less than 14 characters")
        String newPassword
) {
}
