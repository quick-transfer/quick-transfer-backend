package com.weg.quicktransfer.dto.student;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record StudentUpdateRequestDTO(
    @Size(max = 100, message = "The name can have a maximum of 100 characters")
    String name,

    @Email(message = "Email must be a valid email address")
    String email,

    @Min(value = 16, message = "The age must be over 16 and under 19.")
    @Max(value = 19, message = "The age must be over 16 and under 19.")
    Long age,

    @PositiveOrZero(message = "The average cannot be negative")
    Double averageGrade,

    @Positive(message = "Class id must be a positive number")
    Long classId,

    String statusStudentInterview,

    Boolean hasSeenEmail,

    String statusStudent
) {
}
