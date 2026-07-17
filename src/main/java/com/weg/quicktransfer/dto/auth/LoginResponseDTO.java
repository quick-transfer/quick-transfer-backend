package com.weg.quicktransfer.dto.auth;

public record LoginResponseDTO(
        String token,
        String type
) {
}
