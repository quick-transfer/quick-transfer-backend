package com.weg.quicktransfer.dto.vacancy;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record VacancyUpdateRequestDTO(
    String name,

    String description,

    String area,

    String shift,

    UUID placeId,

    List<@NotNull(message = "Skill id must not be null") UUID> skillIds
) {
}
