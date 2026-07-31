package com.weg.quicktransfer.dto.interview;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.Future;

public record InterviewUpdateRequestDTO(
    String interviewerName,

    @Future(message = "Date and time must be in the future")
    LocalDateTime dateTime,

    UUID placeId,

    UUID studentId,

    UUID managerId,

    UUID vacancyId
) {
}
