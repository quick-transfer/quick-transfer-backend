package com.weg.quicktransfer.dto.settings;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

public record SystemSettingsUpdateRequestDTO(
        @Positive(message = "Default capacity must be positive") Integer defaultShiftCapacity,
        @Min(value = 1, message = "High demand percentage must be at least 1")
        @Max(value = 100, message = "High demand percentage cannot exceed 100")
        Integer highDemandPercentage,
        @Email(message = "Email sender must be valid") String emailSender) {
}
