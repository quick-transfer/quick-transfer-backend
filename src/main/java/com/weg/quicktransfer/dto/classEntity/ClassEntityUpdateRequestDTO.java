package com.weg.quicktransfer.dto.classEntity;

import java.time.LocalDate;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Pattern;

public record ClassEntityUpdateRequestDTO(
    Long courseId,

    @FutureOrPresent
    LocalDate startDate,

    @Future
    LocalDate finishDate,
    
    @Pattern(regexp = "^[A-Z0-9-]+$")
    String acronym
) {
}
