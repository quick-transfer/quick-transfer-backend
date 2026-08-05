package com.weg.quicktransfer.dto.vacancy;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record VacancySkillRequestDTO(
        @NotBlank(message = "Skill name must not be blank")
        String name,

        @NotBlank(message = "Skill type must not be blank")
        String skillType,

        @NotNull(message = "Minimum grade must not be null")
        @PositiveOrZero(message = "Minimum grade cannot be negative")
        @DecimalMax(value = "10.0", message = "Minimum grade cannot be greater than 10")
        Double minimumGrade
) {
}
