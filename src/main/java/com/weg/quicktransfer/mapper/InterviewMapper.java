package com.weg.quicktransfer.mapper;

import org.springframework.stereotype.Component;

import com.weg.quicktransfer.dto.interview.InterviewRequestDTO;
import com.weg.quicktransfer.dto.interview.InterviewResponseDTO;
import com.weg.quicktransfer.model.Interview;
import com.weg.quicktransfer.model.Manager;
import com.weg.quicktransfer.model.Place;
import com.weg.quicktransfer.model.Student;
import com.weg.quicktransfer.model.Vacancy;

@Component
public class InterviewMapper {
    public Interview toEntity(InterviewRequestDTO interviewRequestDTO, Place place, Vacancy vacancy, Manager manager, Student student) {
        return new Interview(
            interviewRequestDTO.dateTime(),
            vacancy,
            place,
            manager,
            student
        );
    }

    public InterviewResponseDTO toResponse(Interview interview) {
        return new InterviewResponseDTO(
            interview.getId(),
            interview.getDateTime(),
            interview.getPlace().getPark().name(),
            interview.getPlace().getSection().name(),
            interview.getStudent().getName(),
            interview.getManager().getName(),
            interview.getVacancy().getShift().name()
        );
    }
}
