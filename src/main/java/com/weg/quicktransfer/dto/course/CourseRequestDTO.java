package com.weg.quicktransfer.dto.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CourseRequestDTO(
        @NotBlank(message = "Course name must not be empty")
        String name,

        @NotNull(message = "Coordinator id must not be null")
        UUID coordinatorId
) {
}
