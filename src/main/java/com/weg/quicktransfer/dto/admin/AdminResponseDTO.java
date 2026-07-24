package com.weg.quicktransfer.dto.admin;

import com.weg.quicktransfer.dto.user.UserResponseDTO;

public record AdminResponseDTO(
        Long id,

        String name,

        String username,

        String email

) implements UserResponseDTO  {
    @Override
    public Long getId() {
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
