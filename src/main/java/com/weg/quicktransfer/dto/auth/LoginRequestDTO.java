package com.weg.quicktransfer.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(
        @NotBlank(message = "Username can't be blank")
        String username,

        @NotBlank(message = "Password can't be blank")
        @Size(min = 14, message = "Password can't have less than 14 characters")
        String password
) {
}