package com.weg.quicktransfer.dto.skill;

public record SkillResponseDTO(
    Long id,

    String name,

    String skillType,

    Double grade,

    String studentName
) {
}
