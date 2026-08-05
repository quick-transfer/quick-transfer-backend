package com.weg.quicktransfer.dto.vacancy;

import com.weg.quicktransfer.enums.SkillType;

public record VacancySkillFilter(
        String name,
        SkillType skillType,
        Double minimumGrade
) {
}
