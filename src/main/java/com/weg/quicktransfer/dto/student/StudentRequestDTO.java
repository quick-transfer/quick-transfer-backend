package com.weg.quicktransfer.dto.student;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record StudentRequestDTO(
    @NotBlank
    String name,
    @NotBlank
    String email,
    @PositiveOrZero
    Double averageGrade,
    @NotNull
    @Positive
    Long classId,
    @NotBlank
    String statusStudent,
    @NotNull
    Boolean hasSeenEmail
) {
}
