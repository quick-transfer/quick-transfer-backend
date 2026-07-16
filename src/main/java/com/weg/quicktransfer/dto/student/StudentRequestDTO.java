package com.weg.quicktransfer.dto.student;

public record StudentRequestDTO(
    String name,
    String email,
    Double averageGrade,
    Long classId,
    String statusStudent,
    Boolean hasSeenEmail,
    Long interviewId
) {
}
