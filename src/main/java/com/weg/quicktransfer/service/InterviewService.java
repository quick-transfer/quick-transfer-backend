package com.weg.quicktransfer.service;

import java.util.List;

import com.weg.quicktransfer.dto.interview.InterviewFilter;
import com.weg.quicktransfer.model.*;
import com.weg.quicktransfer.repo.specifications.InterviewSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.weg.quicktransfer.dto.interview.InterviewRequestDTO;
import com.weg.quicktransfer.dto.interview.InterviewResponseDTO;
import com.weg.quicktransfer.dto.interview.InterviewUpdateRequestDTO;
import com.weg.quicktransfer.exception.InterviewNotFoundException;
import com.weg.quicktransfer.exception.PlaceNotFoundException;
import com.weg.quicktransfer.exception.StudentNotFoundException;
import com.weg.quicktransfer.exception.UserNotFoundException;
import com.weg.quicktransfer.exception.VacancyNotFoundException;
import com.weg.quicktransfer.mapper.InterviewMapper;
import com.weg.quicktransfer.repo.InterviewRepository;
import com.weg.quicktransfer.repo.ManagerRepository;
import com.weg.quicktransfer.repo.PlaceRepository;
import com.weg.quicktransfer.repo.StudentRepository;
import com.weg.quicktransfer.repo.VacancyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InterviewService {
    private final InterviewRepository interviewRepository;
    private final InterviewMapper interviewMapper;
    private final PlaceRepository placeRepository;
    private final VacancyRepository vacancyRepository;
    private final ManagerRepository managerRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public InterviewResponseDTO create(InterviewRequestDTO interviewRequestDTO) {
        Place place = placeRepository.findById(interviewRequestDTO.placeId()).orElseThrow(() -> new PlaceNotFoundException(interviewRequestDTO.placeId()));

        Vacancy vacancy = vacancyRepository.findById(interviewRequestDTO.vacancyId()).orElseThrow(() -> new VacancyNotFoundException(interviewRequestDTO.vacancyId()));

        Manager manager = managerRepository.findById(interviewRequestDTO.managerId()).orElseThrow(() -> new UserNotFoundException(interviewRequestDTO.managerId()));

        Student student = studentRepository.findById(interviewRequestDTO.studentId()).orElseThrow(() -> new StudentNotFoundException(interviewRequestDTO.studentId()));

        Interview interview = interviewMapper.toEntity(interviewRequestDTO, place, vacancy, manager, student);

        interviewRepository.save(interview);

        return interviewMapper.toResponse(interview);
    }

    @Transactional(readOnly = true)
    public List<InterviewResponseDTO> findAll() {
        List<Interview> interviews = interviewRepository.findAll();

        return interviews.stream().map(interviewMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public InterviewResponseDTO findById(Long id) {
        Interview interview = interviewRepository.findById(id).orElseThrow(() -> new InterviewNotFoundException(id));

        return interviewMapper.toResponse(interview);
    }

    @Transactional(readOnly = true)
    public List<InterviewResponseDTO> searchInterviews(InterviewFilter filter) {
        Specification<Interview> spec = InterviewSpecification.getFilteredInterviews(filter);

        List<Interview> interviews = interviewRepository.findAll(spec);

        return interviews.stream()
                .map(interviewMapper::toResponse)
                .toList();
    }

    @Transactional
    public InterviewResponseDTO update(Long id, InterviewUpdateRequestDTO interviewUpdateRequestDTO) {
        Interview interview = interviewRepository.findById(id).orElseThrow(() -> new InterviewNotFoundException(id));

        if(interviewUpdateRequestDTO.interviewerName() != null && !interviewUpdateRequestDTO.interviewerName().isBlank()) {
            interview.setInterviewerName(interviewUpdateRequestDTO.interviewerName());
        }

        if(interviewUpdateRequestDTO.dateTime() != null) {
            interview.setDateTime(interviewUpdateRequestDTO.dateTime());
        }

        if(interviewUpdateRequestDTO.placeId() != null) {
            Place place = placeRepository.findById(interviewUpdateRequestDTO.placeId()).orElseThrow(() -> new PlaceNotFoundException(interviewUpdateRequestDTO.placeId()));
            interview.setPlace(place);
        }

        if(interviewUpdateRequestDTO.vacancyId() != null) {
            Vacancy vacancy = vacancyRepository.findById(interviewUpdateRequestDTO.vacancyId()).orElseThrow(() -> new VacancyNotFoundException(interviewUpdateRequestDTO.vacancyId()));
            interview.setVacancy(vacancy);
        }

        if(interviewUpdateRequestDTO.managerId() != null) {
            Manager manager = managerRepository.findById(interviewUpdateRequestDTO.managerId()).orElseThrow(() -> new UserNotFoundException(interviewUpdateRequestDTO.managerId()));
            interview.setManager(manager);
        }

        if(interviewUpdateRequestDTO.studentId() != null) {
            Student student = studentRepository.findById(interviewUpdateRequestDTO.studentId()).orElseThrow(() -> new StudentNotFoundException(interviewUpdateRequestDTO.studentId()));
            interview.setStudent(student);
        }

        Interview interviewAtt = interviewRepository.save(interview);

        return interviewMapper.toResponse(interviewAtt);
    }

    @Transactional
    public void delete(Long id) {
        if(!interviewRepository.existsById(id)) {
            throw new InterviewNotFoundException(id);
        }

        interviewRepository.deleteById(id);
    }
}
