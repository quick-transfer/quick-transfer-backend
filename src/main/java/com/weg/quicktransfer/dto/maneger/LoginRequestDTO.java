package com.weg.quicktransfer.dto.maneger;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank
        String username,

        @NotBlank
        String password
) {
}
