package com.weg.quicktransfer.dto.skill;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public record SkillUpdateRequestDTO(
    String name,

    String skillType,

    @PositiveOrZero(message = "The grade cannot be negative")
    @DecimalMax(value = "10.0", message = "The grade cannot be greater than 10")
    Double grade,

    UUID studentId
) {
}
