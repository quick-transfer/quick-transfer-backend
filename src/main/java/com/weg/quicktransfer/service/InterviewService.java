package com.weg.quicktransfer.service;

import java.util.List;
import java.util.UUID;
import java.util.Locale;

import com.weg.quicktransfer.dto.interview.InterviewFilter;
import com.weg.quicktransfer.model.*;
import com.weg.quicktransfer.repo.specifications.InterviewSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import com.weg.quicktransfer.enums.ApplicationStatus;
import com.weg.quicktransfer.enums.InterviewOutcome;
import com.weg.quicktransfer.enums.InterviewStatus;
import com.weg.quicktransfer.enums.Role;
import com.weg.quicktransfer.enums.StudentInterviewStatus;
import com.weg.quicktransfer.exception.VacancyApplicationNotFoundException;
import com.weg.quicktransfer.repo.VacancyApplicationRepository;
import com.weg.quicktransfer.security.UserPrincipal;
import org.springframework.security.access.AccessDeniedException;

@Service
@RequiredArgsConstructor
public class InterviewService {
    private final InterviewRepository interviewRepository;
    private final InterviewMapper interviewMapper;
    private final PlaceRepository placeRepository;
    private final VacancyRepository vacancyRepository;
    private final ManagerRepository managerRepository;
    private final StudentRepository studentRepository;
    private final VacancyApplicationRepository applicationRepository;

    @Transactional
    public InterviewResponseDTO create(InterviewRequestDTO interviewRequestDTO) {
        return create(interviewRequestDTO, null);
    }

    @Transactional
    public InterviewResponseDTO create(
            InterviewRequestDTO interviewRequestDTO,
            UserPrincipal principal) {
        Vacancy vacancy;
        Place place;
        if (interviewRequestDTO.placeId() != null) {
            place = placeRepository.findById(interviewRequestDTO.placeId())
                    .orElseThrow(() -> new PlaceNotFoundException(interviewRequestDTO.placeId()));
            vacancy = vacancyRepository.findById(interviewRequestDTO.vacancyId())
                    .orElseThrow(() -> new VacancyNotFoundException(interviewRequestDTO.vacancyId()));
        } else {
            vacancy = vacancyRepository.findById(interviewRequestDTO.vacancyId())
                    .orElseThrow(() -> new VacancyNotFoundException(interviewRequestDTO.vacancyId()));
            place = vacancy.getPlace();
        }

        UUID managerId = principal != null && principal.getRole() == Role.MANAGER
                ? principal.getId()
                : interviewRequestDTO.managerId();
        if (managerId == null) {
            throw new IllegalArgumentException("Manager ID is required for administrator scheduling");
        }
        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new UserNotFoundException(managerId));

        Student student = studentRepository.findById(interviewRequestDTO.studentId()).orElseThrow(() -> new StudentNotFoundException(interviewRequestDTO.studentId()));

        validateRelationships(place, vacancy, manager, student, null);

        VacancyApplication application = resolveApplication(
                interviewRequestDTO.applicationId(), vacancy, student, manager);

        Interview interview = interviewMapper.toEntity(interviewRequestDTO, place, vacancy, manager, student);
        interview.setNotes(interviewRequestDTO.notes());
        interview.setApplication(application);

        interview = interviewRepository.save(interview);

        if (application != null) {
            application.setStatus(ApplicationStatus.INTERVIEW_SCHEDULED);
            application.setInterview(interview);
            applicationRepository.save(application);
        }

        return interviewMapper.toResponse(interview);
    }

    @Transactional(readOnly = true)
    public List<InterviewResponseDTO> findAll() {
        List<Interview> interviews = interviewRepository.findAll();

        return interviews.stream().map(interviewMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<InterviewResponseDTO> findAll(Pageable pageable) {
        return interviewRepository.findAll(pageable).map(interviewMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<InterviewResponseDTO> findAll(Pageable pageable, UserPrincipal principal) {
        if (principal != null && principal.getRole() == Role.MANAGER) {
            return interviewRepository.findAllByManagerId(principal.getId(), pageable)
                    .map(interviewMapper::toResponse);
        }
        return findAll(pageable);
    }

    @Transactional(readOnly = true)
    public InterviewResponseDTO findById(UUID id) {
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

    @Transactional(readOnly = true)
    public Page<InterviewResponseDTO> searchInterviews(InterviewFilter filter, Pageable pageable) {
        Specification<Interview> spec = InterviewSpecification.getFilteredInterviews(filter);
        return interviewRepository.findAll(spec, pageable).map(interviewMapper::toResponse);
    }

    @Transactional
    public InterviewResponseDTO update(UUID id, InterviewUpdateRequestDTO interviewUpdateRequestDTO) {
        return update(id, interviewUpdateRequestDTO, null);
    }

    @Transactional
    public InterviewResponseDTO update(
            UUID id,
            InterviewUpdateRequestDTO interviewUpdateRequestDTO,
            UserPrincipal principal) {
        Interview interview = interviewRepository.findById(id).orElseThrow(() -> new InterviewNotFoundException(id));
        validateManagerAccess(interview, principal);

        if(interviewUpdateRequestDTO.interviewerName() != null && !interviewUpdateRequestDTO.interviewerName().isBlank()) {
            interview.setInterviewerName(interviewUpdateRequestDTO.interviewerName());
        }

        if(interviewUpdateRequestDTO.dateTime() != null) {
            interview.setDateTime(interviewUpdateRequestDTO.dateTime());
            interview.setReminderSent(false);
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
            if (principal != null && principal.getRole() == Role.MANAGER) {
                throw new AccessDeniedException("Managers cannot reassign interviews");
            }
            Manager manager = managerRepository.findById(interviewUpdateRequestDTO.managerId()).orElseThrow(() -> new UserNotFoundException(interviewUpdateRequestDTO.managerId()));
            interview.setManager(manager);
        }

        if(interviewUpdateRequestDTO.studentId() != null) {
            Student student = studentRepository.findById(interviewUpdateRequestDTO.studentId()).orElseThrow(() -> new StudentNotFoundException(interviewUpdateRequestDTO.studentId()));
            interview.setStudent(student);
            interview.setReminderSent(false);
        }

        if (interviewUpdateRequestDTO.notes() != null) {
            interview.setNotes(interviewUpdateRequestDTO.notes());
        }

        if (interviewUpdateRequestDTO.applicationId() != null) {
            interview.setApplication(resolveApplication(
                    interviewUpdateRequestDTO.applicationId(),
                    interview.getVacancy(), interview.getStudent(), interview.getManager()));
        }

        if (interviewUpdateRequestDTO.status() != null
                && !interviewUpdateRequestDTO.status().isBlank()) {
            interview.setStatus(InterviewStatus.valueOf(
                    interviewUpdateRequestDTO.status().trim().toUpperCase(Locale.ROOT)));
        }

        if (interviewUpdateRequestDTO.outcome() != null
                && !interviewUpdateRequestDTO.outcome().isBlank()) {
            applyOutcome(interview, InterviewOutcome.valueOf(
                    interviewUpdateRequestDTO.outcome().trim().toUpperCase(Locale.ROOT)));
        }

        validateRelationships(
                interview.getPlace(),
                interview.getVacancy(),
                interview.getManager(),
                interview.getStudent(),
                interview.getId());

        Interview interviewAtt = interviewRepository.save(interview);

        return interviewMapper.toResponse(interviewAtt);
    }

    @Transactional
    public void delete(UUID id) {
        delete(id, null);
    }

    @Transactional
    public void delete(UUID id, UserPrincipal principal) {
        if(!interviewRepository.existsById(id)) {
            throw new InterviewNotFoundException(id);
        }

        if (principal != null && principal.getRole() == Role.MANAGER) {
            Interview interview = interviewRepository.findById(id)
                    .orElseThrow(() -> new InterviewNotFoundException(id));
            validateManagerAccess(interview, principal);
        }

        interviewRepository.deleteById(id);
    }

    private void validateRelationships(
            Place place,
            Vacancy vacancy,
            Manager manager,
            Student student,
            UUID currentInterviewId) {

        if (!vacancy.getPlace().getId().equals(place.getId())) {
            throw new IllegalArgumentException("The vacancy does not belong to the selected place");
        }

        if (manager.getSection() != place.getSection()) {
            throw new IllegalArgumentException("The manager does not belong to the selected place section");
        }

        if (vacancy.getManager() != null
                && !vacancy.getManager().getId().equals(manager.getId())) {
            throw new IllegalArgumentException("The manager is not responsible for this vacancy");
        }

        boolean studentAlreadyScheduled = currentInterviewId == null
                ? interviewRepository.existsByStudentIdAndStatus(
                        student.getId(), InterviewStatus.SCHEDULED)
                : interviewRepository.existsByStudentIdAndStatusAndIdNot(
                        student.getId(), InterviewStatus.SCHEDULED, currentInterviewId);
        if (studentAlreadyScheduled) {
            throw new IllegalArgumentException("The student already has an interview");
        }

        long hiredStudents = interviewRepository.countByVacancyIdAndOutcome(
                vacancy.getId(), InterviewOutcome.APPROVED);
        if (hiredStudents >= vacancy.getNumbersVacancies()) {
            throw new IllegalArgumentException("There are no available positions for this vacancy");
        }
    }

    private VacancyApplication resolveApplication(
            UUID applicationId,
            Vacancy vacancy,
            Student student,
            Manager manager) {
        if (applicationId == null) {
            return null;
        }
        VacancyApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new VacancyApplicationNotFoundException(applicationId));
        if (!application.getVacancy().getId().equals(vacancy.getId())
                || !application.getStudent().getId().equals(student.getId())) {
            throw new IllegalArgumentException("Application does not match the interview student and vacancy");
        }
        if (vacancy.getManager() == null
                || !vacancy.getManager().getId().equals(manager.getId())) {
            throw new AccessDeniedException("Manager cannot schedule this application");
        }
        if (application.getInterview() != null) {
            throw new IllegalArgumentException("Application already has an interview");
        }
        return application;
    }

    private void validateManagerAccess(Interview interview, UserPrincipal principal) {
        if (principal != null && principal.getRole() == Role.MANAGER
                && (interview.getManager() == null
                || !principal.getId().equals(interview.getManager().getId()))) {
            throw new AccessDeniedException("Manager cannot modify another manager's interview");
        }
    }

    private void applyOutcome(Interview interview, InterviewOutcome outcome) {
        interview.setOutcome(outcome);
        if (outcome == InterviewOutcome.PENDING) {
            return;
        }
        interview.setStatus(InterviewStatus.COMPLETED);
        if (outcome == InterviewOutcome.APPROVED) {
            interview.getStudent().setStatus(StudentInterviewStatus.HIRED);
            if (interview.getApplication() != null) {
                interview.getApplication().setStatus(ApplicationStatus.HIRED);
            }
        } else {
            interview.getStudent().setStatus(StudentInterviewStatus.DISAPPROVED);
            if (interview.getApplication() != null) {
                interview.getApplication().setStatus(ApplicationStatus.REJECTED);
            }
        }
    }
}
