package com.weg.quicktransfer.dto.manager;

public record ManagerRequestDTO(
    String name,
    String userName,
    String email,
    String password,
    String role,
    String section
) {
}
