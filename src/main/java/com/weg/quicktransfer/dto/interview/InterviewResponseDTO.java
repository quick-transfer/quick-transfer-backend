package com.weg.quicktransfer.dto.interview;

import java.time.LocalDateTime;

public record InterviewResponseDTO(
    Long id,
    String interviewerName,
    LocalDateTime dateTime,
    String park,
    String section,
    String nameStudent,
    String nameManager,
    String shift
) {
}
