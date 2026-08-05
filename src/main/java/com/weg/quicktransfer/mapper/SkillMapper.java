package com.weg.quicktransfer.mapper;

import java.util.Locale;

import org.springframework.stereotype.Component;

import com.weg.quicktransfer.dto.skill.SkillRequestDTO;
import com.weg.quicktransfer.dto.skill.SkillResponseDTO;
import com.weg.quicktransfer.enums.SkillType;
import com.weg.quicktransfer.model.Skill;
import com.weg.quicktransfer.model.Student;

@Component
public class SkillMapper {
    public Skill toEntity(SkillRequestDTO skillRequestDTO, Student student) {
        return new Skill(
            skillRequestDTO.name(),
            SkillType.valueOf(skillRequestDTO.skillType().trim().toUpperCase(Locale.ROOT)),
            skillRequestDTO.grade(),
            student
        );
    }

    public SkillResponseDTO toResponse(Skill skill) {
        return new SkillResponseDTO(
            skill.getId(),
            skill.getName(),
            skill.getSkillType().name(),
            skill.getGrade(),
            skill.getStudent().getName()
        );
    }
}
