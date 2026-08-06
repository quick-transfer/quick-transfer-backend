package com.weg.quicktransfer.dto.settings;

import java.time.LocalDateTime;

public record SystemSettingsResponseDTO(
        Integer defaultShiftCapacity,
        Integer highDemandPercentage,
        String emailSender,
        LocalDateTime updatedAt,
        String updatedByName) {
}
