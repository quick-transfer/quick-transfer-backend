package com.weg.quicktransfer.dto.application;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record VacancyApplicationRequestDTO(
        @NotNull UUID vacancyId,
        @NotNull UUID studentId,
        String notes
) {
}
