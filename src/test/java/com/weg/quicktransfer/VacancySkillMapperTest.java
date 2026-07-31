package com.weg.quicktransfer;

import com.weg.quicktransfer.dto.vacancy.VacancySkillRequestDTO;
import com.weg.quicktransfer.dto.vacancy.VacancySkillResponseDTO;
import com.weg.quicktransfer.enums.SkillType;
import com.weg.quicktransfer.mapper.VacancySkillMapper;
import com.weg.quicktransfer.model.VacancySkill;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VacancySkillMapperTest {

    private final VacancySkillMapper vacancySkillMapper = new VacancySkillMapper();

    @Test
    void shouldMapRequestAndResponse() {
        VacancySkillRequestDTO requestDTO =
                new VacancySkillRequestDTO("Java", SkillType.TECHNICAL.name(), 7.0);

        VacancySkill vacancySkill = vacancySkillMapper.toEntity(requestDTO);

        assertEquals("Java", vacancySkill.getName());
        assertEquals(SkillType.TECHNICAL, vacancySkill.getSkillType());
        assertEquals(7.0, vacancySkill.getMinimumGrade());

        UUID skillId = UUID.randomUUID();
        vacancySkill.setId(skillId);

        VacancySkillResponseDTO responseDTO = vacancySkillMapper.toResponse(vacancySkill);

        assertEquals(skillId, responseDTO.id());
        assertEquals("Java", responseDTO.name());
        assertEquals(SkillType.TECHNICAL.name(), responseDTO.skillType());
        assertEquals(7.0, responseDTO.minimumGrade());
    }
}
