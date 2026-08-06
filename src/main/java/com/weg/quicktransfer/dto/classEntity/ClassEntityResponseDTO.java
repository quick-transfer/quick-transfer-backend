package com.weg.quicktransfer.dto.classEntity;

import java.time.LocalDate;
import java.util.UUID;

public record ClassEntityResponseDTO(
        UUID id,

        String courseName,

        LocalDate startDate,

        LocalDate finishDate,

        String status,

        String shiftClass,

        String acronym,

        String name,

        Long maxStudents,

        long totalStudents
) {
    public ClassEntityResponseDTO(UUID id, String courseName, LocalDate startDate,
            LocalDate finishDate, String status, String shiftClass, String acronym) {
        this(id, courseName, startDate, finishDate, status, shiftClass, acronym,
                acronym, 30L, 0);
    }
}
