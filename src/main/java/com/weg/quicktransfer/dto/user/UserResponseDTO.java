package com.weg.quicktransfer.dto.user;

import java.util.UUID;

public interface UserResponseDTO {
    UUID getId();

    String getName();

    String getUsername();
    
    String getEmail();
}
