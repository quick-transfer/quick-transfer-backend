package com.weg.quicktransfer.dto.interview;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;

public record InterviewUpdateRequestDTO(
    String interviewerName,

    @Future
    LocalDateTime dateTime,

    @Positive(message = "Place ID must be a positive number")
    Long placeId,

    @Positive(message = "Student ID must be a positive number")
    Long studentId,

    @Positive(message = "Manager ID must be a positive number")
    Long managerId,

    @Positive(message = "Vacancy ID must be a positive number")
    Long vacancyId
) {
}
