package com.weg.quicktransfer.dto.auth;

import com.weg.quicktransfer.enums.Role;

import java.util.UUID;

public record AuthenticatedUserResponseDTO(
        UUID id,
        String name,
        String username,
        Role role
) {
}
