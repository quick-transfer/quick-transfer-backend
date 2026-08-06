package com.weg.quicktransfer.dto.transfer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TransferRequestCreateDTO(
        @NotNull(message = "Student ID must not be null") UUID studentId,
        @NotNull(message = "Target shift ID must not be null") UUID targetShiftId,
        @NotBlank(message = "Reason must not be blank") String reason) {
}
