package com.weg.quicktransfer.dto.course;

import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CourseUpdateRequestDTO(
    @Size(max = 100, message = "The name can have a maximum of 100 characters")
    String name,

    UUID coordinatorId,

    String code,

    String status
) {
    public CourseUpdateRequestDTO(String name, UUID coordinatorId) {
        this(name, coordinatorId, null, null);
    }
}
