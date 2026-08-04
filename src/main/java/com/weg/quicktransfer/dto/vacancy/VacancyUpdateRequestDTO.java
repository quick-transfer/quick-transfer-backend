package com.weg.quicktransfer.dto.vacancy;

import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record VacancyUpdateRequestDTO(
    String name,

    String description,

    String area,

    String shift,

    @Positive(message = "The number of vacancies must be positive")
    Long numbersVacancies,

    UUID placeId
) {
}
