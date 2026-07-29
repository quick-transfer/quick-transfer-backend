package com.weg.quicktransfer.dto.student;

import java.util.UUID;

public record StudentResponseDTO(

    UUID id,

    String name,

    String email,

    Long age,

    Double averageGrade,

    String acronym,

    String course,

    String statusStudentInterview,
    
    Boolean hasSeenEmail,

    String statusStudent
) {
}
