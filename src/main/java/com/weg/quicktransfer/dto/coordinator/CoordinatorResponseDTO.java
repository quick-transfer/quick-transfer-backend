package com.weg.quicktransfer.dto.coordinator;

import com.weg.quicktransfer.dto.user.UserResponseDTO;

import java.util.UUID;

public record CoordinatorResponseDTO(
        UUID id,

        String name,

        String username,

        String email
)  implements UserResponseDTO {
    @Override
    public UUID getId() {
        return this.id();
    }

    @Override
    public String getName() {
        return this.name();
    }

    @Override
    public String getUsername() {
        return this.username();
    }

    @Override
    public String getEmail() {
        return this.email();
    }
}