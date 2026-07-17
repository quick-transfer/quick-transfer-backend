package com.weg.quicktransfer.dto.course;

import jakarta.validation.constraints.NotBlank;

public record CourseRequestDTO(

        @NotBlank(message = "Course name must not be empty")
        String name,

        @NotBlank(message = "Coordinator id must not be null")
        Long coordinatorId
) {
}
