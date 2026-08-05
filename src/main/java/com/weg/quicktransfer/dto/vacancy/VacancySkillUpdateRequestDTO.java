package com.weg.quicktransfer.dto.vacancy;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.PositiveOrZero;

public record VacancySkillUpdateRequestDTO(
        String name,

        String skillType,

        @PositiveOrZero(message = "Minimum grade cannot be negative")
        @DecimalMax(value = "10.0", message = "Minimum grade cannot be greater than 10")
        Double minimumGrade
) {
}
