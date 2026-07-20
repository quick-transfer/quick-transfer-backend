package com.weg.quicktransfer.dto.classEntity;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record ClassEntityRequestDTO(

        @NotNull(message = "Course id must not be null")
        @Positive
        Long courseId,

        @NotNull(message = "Finish date must not be null")
        @Future
        LocalDate finishDate,

        @NotBlank(message = "Class acronym must not be empty")
        @Pattern(regexp = "^[A-Z0-9-]+$")
        String acronym
) {
}
