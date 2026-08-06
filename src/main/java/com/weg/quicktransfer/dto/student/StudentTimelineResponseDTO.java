package com.weg.quicktransfer.dto.student;

import java.time.LocalDateTime;
import java.util.UUID;

public record StudentTimelineResponseDTO(
        String id,
        UUID studentId,
        String title,
        String description,
        LocalDateTime date,
        String type,
        String status) {
}
