package com.weg.quicktransfer.dto.classEntity;

import java.time.LocalDate;

public record ClassEntityResponseDTO(
        Long id,

        String courseName,

        LocalDate startDate,

        LocalDate finishDate,

        String acronym
) {
}
