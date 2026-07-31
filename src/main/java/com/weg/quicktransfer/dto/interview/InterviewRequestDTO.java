package com.weg.quicktransfer.dto.interview;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record InterviewRequestDTO(
    @NotBlank(message = "The interviewer name must not be null or empty")
    String interviewerName,

    @NotNull(message = "Date and time must not be null")
    @Future(message = "Date and time must be in the future")
    LocalDateTime dateTime,

    @NotNull(message = "Place ID must not be null")
    UUID placeId,

    @NotNull(message = "Student ID must not be null")
    UUID studentId,

    @NotNull(message = "Manager ID must not be null")
    UUID managerId,

    @NotNull(message = "Vacancy ID must not be null")
    UUID vacancyId
) {
}
