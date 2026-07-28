package com.weg.quicktransfer.dto.skill;

import java.util.UUID;

public record SkillResponseDTO(
    UUID id,

    String name,

    String skillType,

    Double grade,

    String studentName
) {
}
