package com.weg.quicktransfer.dto.classEntity;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Pattern;

public record ClassEntityUpdateRequestDTO(
    UUID courseId,

    @FutureOrPresent(message = "Start date must be today or in the future")
    LocalDate startDate,

    @Future(message = "Finish date must be in the future")
    LocalDate finishDate,

    String status,

    String shiftClass,

    @Pattern(regexp = "^[A-Z0-9-]+$")
    String acronym
) {
}
