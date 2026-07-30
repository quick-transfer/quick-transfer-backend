package com.weg.quicktransfer.dto.vacancy;

import java.util.UUID;

public record VacancyResponseDTO(
    UUID id,

    String name,

    String description,

    Long numbersVacancies,

    String area,

    String shift,

    String park,
    
    String section
) {
}
