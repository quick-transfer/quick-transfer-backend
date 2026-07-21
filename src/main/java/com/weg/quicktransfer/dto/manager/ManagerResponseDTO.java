package com.weg.quicktransfer.dto.manager;


public record ManagerResponseDTO(
    Long id,

    String name,

    String username,

    String email,
    
    String section
) {
}
