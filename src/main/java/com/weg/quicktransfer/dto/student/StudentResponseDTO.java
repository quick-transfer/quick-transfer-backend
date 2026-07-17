package com.weg.quicktransfer.dto.student;

public record StudentResponseDTO(
    Long id,
    String name,
    String email,
    Double averageGrade,
    String acronym,
    String course,
    String statusStudent,
    Boolean hasSeenEmail
) {
}
