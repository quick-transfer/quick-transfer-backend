package com.weg.quicktransfer.dto.course;

public record CourseResponseDTO(
        Long id,

        String courseName,

        String coordinatorName,

        String coordinatorEmail
) {
}
