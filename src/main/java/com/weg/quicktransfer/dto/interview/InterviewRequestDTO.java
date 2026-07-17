package com.weg.quicktransfer.dto.interview;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


public record InterviewRequestDTO(
    @NotNull
    @Future
    LocalDateTime dateTime,
    @NotNull
    @Positive
    Long placeId,
    @NotNull
    @Positive
    Long studentId,
    @NotNull
    @Positive
    Long managerId,
    @NotNull
    @Positive
    Long vacancyId
) {
}
