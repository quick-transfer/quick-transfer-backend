package com.weg.quicktransfer.dto.vacancy;

public record VacancyResponseDTO(
    Long id,
    String name,
    String description,
    String area,
    String shift,
    String park,
    String section
) {
}
