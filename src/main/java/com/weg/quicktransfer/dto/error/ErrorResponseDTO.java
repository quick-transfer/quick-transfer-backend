package com.weg.quicktransfer.dto.error;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
    LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {
}
