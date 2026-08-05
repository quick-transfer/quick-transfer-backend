package com.weg.quicktransfer.mapper;

import org.springframework.stereotype.Component;

import com.weg.quicktransfer.dto.vacancy.VacancyRequestDTO;
import com.weg.quicktransfer.dto.vacancy.VacancyResponseDTO;
import com.weg.quicktransfer.enums.Area;
import com.weg.quicktransfer.enums.Shift;
import com.weg.quicktransfer.model.Place;
import com.weg.quicktransfer.model.Vacancy;
import com.weg.quicktransfer.model.VacancySkill;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Component
@RequiredArgsConstructor
public class VacancyMapper {
    private final VacancySkillMapper vacancySkillMapper;

    public Vacancy toEntity(VacancyRequestDTO vacancyRequestDTO, Place place, List<VacancySkill> skills) {
        Vacancy vacancy = new Vacancy(
            vacancyRequestDTO.name(),
            vacancyRequestDTO.description(),
            vacancyRequestDTO.numbersVacancies(),
            Area.valueOf(vacancyRequestDTO.area()),
            Shift.valueOf(vacancyRequestDTO.shift()),
            place
        );

        vacancy.setSkills(skills);
        return vacancy;
    }

    public VacancyResponseDTO toResponse(Vacancy vacancy) {
        return new VacancyResponseDTO(
            vacancy.getId(),
            vacancy.getName(),
            vacancy.getDescription(),
            vacancy.getNumbersVacancies(),
            vacancy.getArea().name(),
            vacancy.getShift().name(),
            vacancy.getPlace().getPark().name(),
            vacancy.getPlace().getSection().name(),
            vacancy.getSkills().stream()
                    .map(vacancySkillMapper::toResponse)
                    .toList()
        );
    }
}
