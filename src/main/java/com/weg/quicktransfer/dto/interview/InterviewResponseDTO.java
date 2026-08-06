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
    
    String shift,

    UUID studentId,

    String studentEmail,

    UUID vacancyId,

    String vacancyName,

    UUID placeId,

    String placeName,

    UUID managerId,

    String notes,

    String status,

    String outcome,

    UUID applicationId
) {
    public InterviewResponseDTO(UUID id, String interviewerName, LocalDateTime dateTime,
            String park, String section, String nameStudent, String nameManager, String shift) {
        this(id, interviewerName, dateTime, park, section, nameStudent, nameManager, shift,
                null, null, null, null, null, null, null, null, "SCHEDULED", "PENDING", null);
    }
}
