package com.weg.quicktransfer.dto.skill;

import com.weg.quicktransfer.enums.SkillType;

public record SkillFilter(
        String name,

        SkillType skillType,

        Double grade,

        String studentName
) {
}
