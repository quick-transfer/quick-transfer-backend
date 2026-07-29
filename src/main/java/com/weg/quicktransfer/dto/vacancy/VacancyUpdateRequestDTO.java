package com.weg.quicktransfer.dto.vacancy;

import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record VacancyUpdateRequestDTO(
    String name,

    String description,

    String area,

    String shift,
    
    @Positive(message = "Place id must be a positive number")
    UUID placeId
) {
}
