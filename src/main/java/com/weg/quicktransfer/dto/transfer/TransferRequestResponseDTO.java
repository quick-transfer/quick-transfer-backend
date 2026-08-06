package com.weg.quicktransfer.dto.transfer;

import java.time.LocalDateTime;
import java.util.UUID;

public record TransferRequestResponseDTO(
        UUID id,
        UUID studentId,
        String studentName,
        String registration,
        UUID currentShiftId,
        String currentShift,
        UUID targetShiftId,
        String targetShift,
        String reason,
        String status,
        LocalDateTime requestedAt,
        UUID requestedById,
        String requestedByName,
        LocalDateTime resolvedAt,
        UUID resolvedById,
        String resolvedByName,
        String resolutionNotes) {
}
