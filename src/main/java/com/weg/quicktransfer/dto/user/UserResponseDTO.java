package com.weg.quicktransfer.dto.user;

import com.weg.quicktransfer.enums.Role;
import java.util.UUID;

public interface UserResponseDTO {
    UUID getId();

    String getName();

    String getUsername();
    
    String getEmail();

    Role getRole();

    Boolean getActive();

    Boolean getFirstLogin();
}
