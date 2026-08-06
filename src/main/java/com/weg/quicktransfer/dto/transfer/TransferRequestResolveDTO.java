package com.weg.quicktransfer.dto.transfer;

import jakarta.validation.constraints.NotBlank;

public record TransferRequestResolveDTO(
        @NotBlank(message = "Status must not be blank") String status,
        String resolutionNotes) {
}
