package com.weg.quicktransfer.dto.interview;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


public record InterviewRequestDTO(
    @NotNull(message = "Date and time must not be null")
    @Future
    LocalDateTime dateTime,
    @NotNull(message = "Place ID must not be null")
    @Positive(message = "Place ID must be a positive number")
    Long placeId,
    @NotNull(message = "Student ID must not be null")
    @Positive(message = "Student ID must be a positive number")
    Long studentId,
    @NotNull(message = "Manager ID must not be null")
    @Positive(message = "Manager ID must be a positive number")
    Long managerId,
    @NotNull(message = "Vacancy ID must not be null")
    @Positive(message = "Vacancy ID must be a positive number")
    Long vacancyId
) {
}
