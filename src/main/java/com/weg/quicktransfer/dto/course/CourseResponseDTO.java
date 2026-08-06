package com.weg.quicktransfer.dto.course;

import java.util.UUID;

public record CourseResponseDTO(
        UUID id,

        String courseName,

        String coordinatorName,

        String coordinatorEmail,

        String code,

        String status,

        long totalStudents
) {
    public CourseResponseDTO(UUID id, String courseName, String coordinatorName,
            String coordinatorEmail) {
        this(id, courseName, coordinatorName, coordinatorEmail, null, "ACTIVE", 0);
    }
}
