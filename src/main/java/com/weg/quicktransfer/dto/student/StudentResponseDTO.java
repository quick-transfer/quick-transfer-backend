package com.weg.quicktransfer.dto.student;

public record StudentResponseDTO(
    Long id,
    String name,
    String email,
    Long age,
    Double averageGrade,
    String acronym,
    String course,
    String statusStudent,
    Boolean hasSeenEmail
) {
}
