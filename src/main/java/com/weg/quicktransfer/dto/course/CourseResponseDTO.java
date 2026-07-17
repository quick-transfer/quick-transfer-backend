package com.weg.quicktransfer.dto.course;

import java.util.List;

public record CourseResponseDTO(
        Long id,
        String courseName,
        String coordinatorName,
        List<String> classesAcronym
) {
}
