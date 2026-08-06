package com.weg.quicktransfer.dto.application;

import java.time.LocalDateTime;
import java.util.UUID;

public record VacancyApplicationResponseDTO(
        UUID id,
        UUID vacancyId,
        String vacancyName,
        UUID studentId,
        String studentName,
        String studentEmail,
        String registration,
        UUID coordinatorId,
        String coordinatorName,
        UUID managerId,
        String managerName,
        String status,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        UUID interviewId
) {
}
