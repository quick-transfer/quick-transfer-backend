package com.weg.quicktransfer.dto.vacancy;

import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record VacancyUpdateRequestDTO(
    String name,

    String description,

    String area,

    String shift,

    UUID placeId
) {
}
