package com.weg.quicktransfer.dto.manager;


public record ManagerResponseDTO(
    Long id,
    String name,
    String userName,
    String email,
    String section
) {
}
