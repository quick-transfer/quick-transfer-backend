package com.weg.quicktransfer.dto.interview;

import java.time.LocalDateTime;


public record InterviewRequestDTO(
    LocalDateTime dateTime,
    Long placeId,
    Long studentId,
    Long managerId,
    Long vacancyId
) {
}
