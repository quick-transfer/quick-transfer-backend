package com.weg.quicktransfer.dto.interview;

import java.time.LocalDateTime;
import java.util.UUID;

public record InterviewResponseDTO(
    UUID id,

    String interviewerName,

    LocalDateTime dateTime,

    String park,

    String section,

    String nameStudent,

    String nameManager,
    
    String shift
) {
}
