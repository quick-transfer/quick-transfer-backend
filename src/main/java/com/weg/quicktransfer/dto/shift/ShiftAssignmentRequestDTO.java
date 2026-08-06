package com.weg.quicktransfer.dto.shift;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ShiftAssignmentRequestDTO(
        @NotNull(message = "Target shift ID must not be null") UUID targetShiftId,
        String reason) {
}
