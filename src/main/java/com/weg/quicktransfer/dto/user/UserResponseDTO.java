package com.weg.quicktransfer.dto.user;

public record UserResponseDTO(
    Long id,
    String name,
    String username,
    String email
) {
}
