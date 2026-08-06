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

    String statusStudent,

    String registration,

    Double attendanceRate,

    String className,

    String shift,

    Double performanceGrade,

    UUID operationalShiftId
) {
    public StudentResponseDTO(UUID id, String name, String email, Long age, Double averageGrade,
            String acronym, String course, String statusStudentInterview, Boolean hasSeenEmail,
            String statusStudent) {
        this(id, name, email, age, averageGrade, acronym, course, statusStudentInterview,
                hasSeenEmail, statusStudent, null, 0.0, acronym, null, averageGrade, null);
    }
}
