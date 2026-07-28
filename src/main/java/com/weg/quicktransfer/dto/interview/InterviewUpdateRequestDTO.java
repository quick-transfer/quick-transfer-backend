package com.weg.quicktransfer.dto.interview;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;

public record InterviewUpdateRequestDTO(
    String interviewerName,

    @Future(message = "Date and time must be in the future")
    LocalDateTime dateTime,

    @Positive(message = "Place ID must be a positive number")
    UUID placeId,

    @Positive(message = "Student ID must be a positive number")
    UUID studentId,

    @Positive(message = "Manager ID must be a positive number")
    UUID managerId,

    @Positive(message = "Vacancy ID must be a positive number")
    UUID vacancyId
) {
}
