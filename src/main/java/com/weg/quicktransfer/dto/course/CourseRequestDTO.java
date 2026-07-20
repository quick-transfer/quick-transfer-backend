package com.weg.quicktransfer.dto.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CourseRequestDTO(

        @NotBlank(message = "Course name must not be empty")
        String name,

        @NotNull(message = "Coordinator id must not be null")
        @Positive
        Long coordinatorId
) {
}
