package com.weg.quicktransfer;

import com.weg.quicktransfer.dto.vacancy.VacancyRequestDTO;
import com.weg.quicktransfer.dto.vacancy.VacancyResponseDTO;
import com.weg.quicktransfer.enums.Area;
import com.weg.quicktransfer.enums.Park;
import com.weg.quicktransfer.enums.Section;
import com.weg.quicktransfer.enums.Shift;
import com.weg.quicktransfer.enums.SkillType;
import com.weg.quicktransfer.mapper.VacancyMapper;
import com.weg.quicktransfer.mapper.VacancySkillMapper;
import com.weg.quicktransfer.model.Place;
import com.weg.quicktransfer.model.Vacancy;
import com.weg.quicktransfer.model.VacancySkill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class VacancyMapperTest {

    private VacancyMapper vacancyMapper;
    private Place place;

    @BeforeEach
    void setUp() {
        vacancyMapper = new VacancyMapper(new VacancySkillMapper());

        place = new Place();
        place.setId(UUID.randomUUID());
        place.setPark(Park.WEG_II);
        place.setSection(Section.IT);
    }

    @Test
    void shouldMapVacancyWithSkillRequirements() {
        VacancySkill javaSkill = new VacancySkill("Java", SkillType.TECHNICAL, 8.0);
        javaSkill.setId(UUID.randomUUID());
        VacancySkill communicationSkill =
                new VacancySkill("Communication", SkillType.SOCIOEMOTIONAL, 6.0);
        communicationSkill.setId(UUID.randomUUID());
        VacancyRequestDTO requestDTO = new VacancyRequestDTO(
                "Backend Java",
                "Backend development vacancy",
                2L,
                Area.IT.name(),
                Shift.FIRST.name(),
                place.getId(),
                List.of(
                        javaSkill.getId(),
                        communicationSkill.getId()
                )
        );

        Vacancy vacancy = vacancyMapper.toEntity(
                requestDTO, place, List.of(javaSkill, communicationSkill));

        assertEquals(2, vacancy.getSkills().size());
        assertEquals("Java", vacancy.getSkills().get(0).getName());
        assertEquals(8.0, vacancy.getSkills().get(0).getMinimumGrade());
        assertSame(vacancy, javaSkill.getVacancies().get(0));

        vacancy.setId(UUID.randomUUID());
        vacancy.getSkills().get(0).setId(UUID.randomUUID());
        vacancy.getSkills().get(1).setId(UUID.randomUUID());

        VacancyResponseDTO responseDTO = vacancyMapper.toResponse(vacancy);

        assertEquals(vacancy.getId(), responseDTO.id());
        assertEquals(2, responseDTO.skills().size());
        assertEquals(SkillType.SOCIOEMOTIONAL.name(), responseDTO.skills().get(1).skillType());
    }

    @Test
    void shouldMapMissingSkillListAsEmpty() {
        VacancyRequestDTO requestDTO = new VacancyRequestDTO(
                "Backend Java",
                "Backend development vacancy",
                2L,
                Area.IT.name(),
                Shift.FIRST.name(),
                place.getId(),
                null
        );

        Vacancy vacancy = vacancyMapper.toEntity(requestDTO, place, List.of());

        assertEquals(List.of(), vacancy.getSkills());
    }
}
