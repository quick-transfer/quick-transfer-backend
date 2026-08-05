package com.weg.quicktransfer.mapper;

import com.weg.quicktransfer.dto.vacancy.VacancySkillRequestDTO;
import com.weg.quicktransfer.dto.vacancy.VacancySkillResponseDTO;
import com.weg.quicktransfer.enums.SkillType;
import com.weg.quicktransfer.model.VacancySkill;
import org.springframework.stereotype.Component;

@Component
public class VacancySkillMapper {

    public VacancySkill toEntity(VacancySkillRequestDTO requestDTO) {
        return new VacancySkill(
                requestDTO.name(),
                SkillType.valueOf(requestDTO.skillType()),
                requestDTO.minimumGrade()
        );
    }

    public VacancySkillResponseDTO toResponse(VacancySkill vacancySkill) {
        return new VacancySkillResponseDTO(
                vacancySkill.getId(),
                vacancySkill.getName(),
                vacancySkill.getSkillType().name(),
                vacancySkill.getMinimumGrade()
        );
    }
}
