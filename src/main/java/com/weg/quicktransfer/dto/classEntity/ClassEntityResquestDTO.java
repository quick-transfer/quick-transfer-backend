package com.weg.quicktransfer.dto.classEntity;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record ClassEntityResquestDTO(

        @NotBlank(message = "Course id must not be empty")
        Long courseId,

        @NotBlank(message = "finish date must not be empty")
        @Future
        LocalDate finishDate,

        @NotBlank(message = "Class acronym must not be empty")
        @Pattern(regexp = "^[A-Z0-9-]+$")
        String acronym
) {
}
