package com.weg.quicktransfer.dto.skill;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public record SkillRequestDTO(
    @NotBlank(message = "Name must not be blank")
    String name,

    @NotBlank(message = "Skill Type must not be blank")
    String skillType,

    @PositiveOrZero(message = "The grade cannot be negative")
    Double grade,

    @NotNull(message = "Student id must not be null")
    @Positive(message = "Student id must be a positive number")
    UUID studentId
) {
}
