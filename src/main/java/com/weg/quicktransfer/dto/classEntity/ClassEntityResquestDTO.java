package com.weg.quicktransfer.dto.classEntity;

import java.time.LocalDate;

public record ClassEntityResquestDTO(
    Long courseId,
    LocalDate finishDate,
    String acronym
) {
}
