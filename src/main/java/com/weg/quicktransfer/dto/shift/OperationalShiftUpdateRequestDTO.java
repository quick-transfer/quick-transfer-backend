package com.weg.quicktransfer.dto.shift;

import jakarta.validation.constraints.Positive;

public record OperationalShiftUpdateRequestDTO(
        String name,
        String supervisorName,
        @Positive(message = "Capacity must be positive") Integer capacity,
        Boolean active) {
}
