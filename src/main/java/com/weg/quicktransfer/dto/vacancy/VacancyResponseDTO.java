package com.weg.quicktransfer.dto.vacancy;

public record VacancyResponseDTO(
    Long id,
    String shift,
    String park,
    String section
) {
}
