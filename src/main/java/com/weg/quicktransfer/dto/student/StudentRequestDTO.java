package com.weg.quicktransfer.dto.student;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public record StudentRequestDTO(
    @NotBlank(message = "Name must not be blank")
    String name,

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be a valid email address")
    String email,

    @NotNull(message = "The age must not be null")
    @Min(value = 16, message = "The age must be over 16 and under 19.")
    @Max(value = 19, message = "The age must be over 16 and under 19.")
    Long age,

    @PositiveOrZero(message = "The average cannot be negative")
    @DecimalMax(value = "10.0", message = "The average cannot be greater than 10")
    Double averageGrade,

    @NotNull(message = "Class ID must not be null")
    UUID classId,

    @NotBlank(message = "Student status must not be blank")
    String statusStudentInterview,

    @NotNull(message = "Email view cannot be null")
    Boolean hasSeenEmail
) {
}
