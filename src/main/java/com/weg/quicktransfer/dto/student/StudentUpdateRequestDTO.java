package com.weg.quicktransfer.dto.student;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record StudentUpdateRequestDTO(
    @Size(max = 100, message = "The name can have a maximum of 100 characters")
    String name,

    @Email(message = "Email must be a valid email address")
    String email,

    @Min(value = 16, message = "The age must be over 16 and under 19.")
    @Max(value = 19, message = "The age must be over 16 and under 19.")
    Long age,

    @PositiveOrZero(message = "The average cannot be negative")
    @DecimalMax(value = "10.0", message = "The average cannot be greater than 10")
    Double averageGrade,

    UUID classId,

    String statusStudentInterview,

    Boolean hasSeenEmail,

    String statusStudent
) {
}
