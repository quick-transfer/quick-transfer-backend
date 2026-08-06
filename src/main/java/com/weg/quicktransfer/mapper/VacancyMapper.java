package com.weg.quicktransfer.mapper;

import org.springframework.stereotype.Component;

import com.weg.quicktransfer.dto.vacancy.VacancyRequestDTO;
import com.weg.quicktransfer.dto.vacancy.VacancyResponseDTO;
import com.weg.quicktransfer.enums.Area;
import com.weg.quicktransfer.enums.Shift;
import com.weg.quicktransfer.enums.InterviewOutcome;
import com.weg.quicktransfer.enums.VacancyStatus;
import com.weg.quicktransfer.model.Manager;
import com.weg.quicktransfer.model.Place;
import com.weg.quicktransfer.model.Vacancy;
import com.weg.quicktransfer.model.VacancySkill;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class VacancyMapper {
    private final VacancySkillMapper vacancySkillMapper;

    public Vacancy toEntity(VacancyRequestDTO vacancyRequestDTO, Place place, List<VacancySkill> skills) {
        return toEntity(vacancyRequestDTO, place, skills, null);
    }

    public Vacancy toEntity(VacancyRequestDTO vacancyRequestDTO, Place place,
            List<VacancySkill> skills, Manager manager) {
        Vacancy vacancy = new Vacancy(
            vacancyRequestDTO.name(),
            vacancyRequestDTO.description(),
            vacancyRequestDTO.numbersVacancies(),
            Area.valueOf(vacancyRequestDTO.area().trim().toUpperCase(Locale.ROOT)),
            Shift.valueOf(vacancyRequestDTO.shift().trim().toUpperCase(Locale.ROOT)),
            vacancyRequestDTO.status() == null || vacancyRequestDTO.status().isBlank()
                    ? VacancyStatus.OPEN
                    : VacancyStatus.valueOf(
                            vacancyRequestDTO.status().trim().toUpperCase(Locale.ROOT)),
            place,
            manager
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
                    .toList(),
            vacancy.getPlace().getPlaceName(),
            vacancy.getStatus().name(),
            vacancy.getManager() == null ? null : vacancy.getManager().getId(),
            vacancy.getManager() == null ? null : vacancy.getManager().getName(),
            vacancy.getInterviews() == null ? 0 : vacancy.getInterviews().stream()
                    .filter(interview -> interview.getStudent() != null
                            && interview.getOutcome() == InterviewOutcome.APPROVED)
                    .count()
        );
    }
}
