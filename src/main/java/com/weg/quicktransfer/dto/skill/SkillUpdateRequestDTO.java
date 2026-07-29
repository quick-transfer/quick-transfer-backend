package com.weg.quicktransfer.dto.skill;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public record SkillUpdateRequestDTO(
    String name,

    String skillType,

    @PositiveOrZero(message = "The grade cannot be negative")
    Double grade,

    @Positive(message = "Student id must be a positive number")
    UUID studentId
) {
}
