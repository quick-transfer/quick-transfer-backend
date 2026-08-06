package com.weg.quicktransfer.dto.vacancy;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.UUID;

public record VacancyUpdateRequestDTO(
    String name,

    String description,

    String area,

    String shift,

    @Positive(message = "The number of vacancies must be positive")
    Long numbersVacancies,

    UUID placeId,

    List<@NotNull(message = "Skill id must not be null") UUID> skillIds,

    UUID managerId,

    String status
) {
    public VacancyUpdateRequestDTO(String name, String description, String area, String shift,
            Long numbersVacancies, UUID placeId, List<UUID> skillIds) {
        this(name, description, area, shift, numbersVacancies, placeId, skillIds, null, null);
    }
}
