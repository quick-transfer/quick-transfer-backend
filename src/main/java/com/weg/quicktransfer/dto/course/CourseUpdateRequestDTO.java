package com.weg.quicktransfer.dto.course;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CourseUpdateRequestDTO(
    @Size(max = 100, message = "The name can have a maximum of 100 characters")
    String name,
    
    @Positive(message = "Coordinator ID must be a positive number")
    UUID coordinatorId
) {
}
