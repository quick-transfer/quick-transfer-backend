package com.weg.quicktransfer.dto.vacancy;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record VacancyRequestDTO(
    @NotBlank(message = "Name must not be blank")
    String name,
    @NotBlank(message = "Description must not be blank")
    String description,
    @NotBlank(message = "Area must not be blank")
    String area,
    @NotBlank(message = "Shift must not be blank")
    String shift,
    @NotNull(message = "Place id must not be null")
    @Positive(message = "Place id must be a positive number")
    Long placeId
) {
}
