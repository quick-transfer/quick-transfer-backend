package com.weg.quicktransfer.dto.classEntity;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record ClassEntityRequestDTO(
        @NotNull(message = "Course id must not be null")
        Long courseId,

        @NotNull(message = "Sart date must not be null")
        @FutureOrPresent(message = "Start date must be today or in the future")
        LocalDate startDate,

        @NotNull(message = "finish date must not be null")
        @Future(message = "Finish date must be in the future")
        LocalDate finishDate,

        @NotBlank(message = "Class acronym must not be empty")
        @Pattern(regexp = "^[A-Z0-9-]+$")
        String acronym
) {
}