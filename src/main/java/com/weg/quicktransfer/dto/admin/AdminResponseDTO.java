package com.weg.quicktransfer.dto.admin;

import com.weg.quicktransfer.dto.user.UserResponseDTO;
import com.weg.quicktransfer.enums.Role;

import java.util.UUID;

public record AdminResponseDTO(
        UUID id,

        String name,

        String username,

        String email,

        Role role,

        Boolean active,

        Boolean firstLogin

) implements UserResponseDTO  {
    public AdminResponseDTO(UUID id, String name, String username, String email) {
        this(id, name, username, email, Role.ADMIN, true, true);
    }

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

    @Override
    public Role getRole() {
        return this.role();
    }

    @Override
    public Boolean getActive() {
        return this.active();
    }

    @Override
    public Boolean getFirstLogin() {
        return this.firstLogin();
    }
}
