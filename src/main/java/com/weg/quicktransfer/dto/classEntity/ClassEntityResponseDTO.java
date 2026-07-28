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

        String acronym
) {
}
