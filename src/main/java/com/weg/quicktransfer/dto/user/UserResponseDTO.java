package com.weg.quicktransfer.dto.user;

public record UserResponseDTO(
    Long id,
    String name,
    String userName,
    String email
) {
}
