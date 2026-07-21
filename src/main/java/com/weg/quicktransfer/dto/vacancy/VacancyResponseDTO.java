package com.weg.quicktransfer.dto.vacancy;

public record VacancyResponseDTO(
    Long id,
    String name,
    String description,
    Long numbersVacancies,
    String area,
    String shift,
    String park,
    String section
) {
}
