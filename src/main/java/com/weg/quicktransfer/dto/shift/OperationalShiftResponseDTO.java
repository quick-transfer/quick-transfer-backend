package com.weg.quicktransfer.dto.shift;

import java.util.UUID;

public record OperationalShiftResponseDTO(
        UUID id,
        String name,
        String code,
        String supervisorName,
        Integer capacity,
        long currentOccupancy,
        int occupancyPercentage,
        String status,
        Boolean active) {
}
