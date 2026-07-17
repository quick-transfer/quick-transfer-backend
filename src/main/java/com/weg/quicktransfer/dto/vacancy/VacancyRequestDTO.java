package com.weg.quicktransfer.dto.vacancy;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record VacancyRequestDTO(
    @NotBlank
    String shift,
    @NotNull
    @Positive
    Long placeId
) {
}
