package com.weg.quicktransfer.mapper;

import org.springframework.stereotype.Component;

import com.weg.quicktransfer.dto.vacancy.VacancyRequestDTO;
import com.weg.quicktransfer.dto.vacancy.VacancyResponseDTO;
import com.weg.quicktransfer.enums.Area;
import com.weg.quicktransfer.enums.Shift;
import com.weg.quicktransfer.model.Place;
import com.weg.quicktransfer.model.Vacancy;

@Component
public class VacancyMapper {
    public Vacancy toEntity(VacancyRequestDTO vacancyRequestDTO, Place place) {
        return new Vacancy(
            vacancyRequestDTO.name(),
            vacancyRequestDTO.description(),
            Area.valueOf(vacancyRequestDTO.area()),
            Shift.valueOf(vacancyRequestDTO.shift()),
            place
        );
    }

    public VacancyResponseDTO toResponse(Vacancy vacancy) {
        return new VacancyResponseDTO(
            vacancy.getId(),
            vacancy.getName(),
            vacancy.getDescription(),
            vacancy.getArea().name(),
            vacancy.getShift().name(),
            vacancy.getPlace().getPark().name(),
            vacancy.getPlace().getSection().name()
        );
    }
}
