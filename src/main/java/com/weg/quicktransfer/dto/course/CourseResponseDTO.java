package com.weg.quicktransfer.dto.course;

import java.util.UUID;

public record CourseResponseDTO(
        UUID id,

        String courseName,

        String coordinatorName,

        String coordinatorEmail
) {
}
