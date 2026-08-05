package com.weg.quicktransfer.dto.vacancy;

import java.util.UUID;

public record VacancySkillResponseDTO(
        UUID id,
        String name,
        String skillType,
        Double minimumGrade
) {
}
