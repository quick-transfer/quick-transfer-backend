package com.weg.quicktransfer.service;

import com.weg.quicktransfer.dto.application.VacancyApplicationRequestDTO;
import com.weg.quicktransfer.dto.application.VacancyApplicationResponseDTO;
import com.weg.quicktransfer.dto.application.VacancyApplicationUpdateRequestDTO;
import com.weg.quicktransfer.enums.ApplicationStatus;
import com.weg.quicktransfer.enums.Role;
import com.weg.quicktransfer.enums.VacancyStatus;
import com.weg.quicktransfer.exception.*;
import com.weg.quicktransfer.mapper.VacancyApplicationMapper;
import com.weg.quicktransfer.model.Coordinator;
import com.weg.quicktransfer.model.Student;
import com.weg.quicktransfer.model.Vacancy;
import com.weg.quicktransfer.model.VacancyApplication;
import com.weg.quicktransfer.repo.CoordinatorRepository;
import com.weg.quicktransfer.repo.StudentRepository;
import com.weg.quicktransfer.repo.VacancyApplicationRepository;
import com.weg.quicktransfer.repo.VacancyRepository;
import com.weg.quicktransfer.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VacancyApplicationService {
    private final VacancyApplicationRepository applicationRepository;
    private final VacancyRepository vacancyRepository;
    private final StudentRepository studentRepository;
    private final CoordinatorRepository coordinatorRepository;
    private final VacancyApplicationMapper applicationMapper;

    @Transactional
    public VacancyApplicationResponseDTO create(
            VacancyApplicationRequestDTO request,
            UserPrincipal principal) {
        if (principal == null || principal.getRole() != Role.COORDINATOR) {
            throw new AccessDeniedException("Only coordinators can refer students");
        }
        Vacancy vacancy = vacancyRepository.findById(request.vacancyId())
                .orElseThrow(() -> new VacancyNotFoundException(request.vacancyId()));
        if (vacancy.getStatus() == VacancyStatus.CLOSED) {
            throw new IllegalArgumentException("Students cannot be referred to a closed vacancy");
        }
        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new StudentNotFoundException(request.studentId()));
        Coordinator coordinator = coordinatorRepository.findById(principal.getId())
                .orElseThrow(() -> new CoordinatorNotFoundException(principal.getId()));
        if (applicationRepository.existsByVacancyIdAndStudentId(vacancy.getId(), student.getId())) {
            throw new IllegalArgumentException("Student was already referred to this vacancy");
        }
        VacancyApplication application = applicationRepository.save(
                new VacancyApplication(vacancy, student, coordinator, request.notes()));
        return applicationMapper.toResponse(application);
    }

    @Transactional(readOnly = true)
    public Page<VacancyApplicationResponseDTO> findAll(
            Pageable pageable,
            UserPrincipal principal) {
        Page<VacancyApplication> applications;
        if (principal != null && principal.getRole() == Role.MANAGER) {
            applications = applicationRepository.findAllByVacancyManagerId(principal.getId(), pageable);
        } else if (principal != null && principal.getRole() == Role.COORDINATOR) {
            applications = applicationRepository.findAllByCoordinatorId(principal.getId(), pageable);
        } else {
            applications = applicationRepository.findAll(pageable);
        }
        return applications.map(applicationMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public VacancyApplicationResponseDTO findById(UUID id, UserPrincipal principal) {
        VacancyApplication application = getAuthorized(id, principal);
        return applicationMapper.toResponse(application);
    }

    @Transactional
    public VacancyApplicationResponseDTO update(
            UUID id,
            VacancyApplicationUpdateRequestDTO request,
            UserPrincipal principal) {
        VacancyApplication application = getAuthorized(id, principal);
        if (request.notes() != null) {
            application.setNotes(request.notes());
        }
        if (request.status() != null && !request.status().isBlank()) {
            ApplicationStatus next = ApplicationStatus.valueOf(
                    request.status().trim().toUpperCase(Locale.ROOT));
            validateTransition(application, next, principal);
            application.setStatus(next);
        }
        return applicationMapper.toResponse(applicationRepository.save(application));
    }

    @Transactional
    public void delete(UUID id, UserPrincipal principal) {
        VacancyApplication application = getAuthorized(id, principal);
        if (application.getInterview() != null) {
            throw new IllegalArgumentException("An application with an interview cannot be deleted");
        }
        applicationRepository.delete(application);
    }

    private VacancyApplication getAuthorized(UUID id, UserPrincipal principal) {
        VacancyApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> new VacancyApplicationNotFoundException(id));
        if (principal == null || principal.getRole() == Role.ADMIN) {
            return application;
        }
        boolean allowed = principal.getRole() == Role.COORDINATOR
                && principal.getId().equals(application.getCoordinator().getId());
        allowed = allowed || principal.getRole() == Role.MANAGER
                && application.getVacancy().getManager() != null
                && principal.getId().equals(application.getVacancy().getManager().getId());
        if (!allowed) {
            throw new AccessDeniedException("User cannot access this vacancy application");
        }
        return application;
    }

    private void validateTransition(
            VacancyApplication application,
            ApplicationStatus next,
            UserPrincipal principal) {
        if (principal == null || principal.getRole() == Role.ADMIN) {
            return;
        }
        if (principal.getRole() == Role.COORDINATOR && next == ApplicationStatus.WITHDRAWN
                && application.getStatus() == ApplicationStatus.REFERRED) {
            return;
        }
        if (principal.getRole() == Role.MANAGER && next == ApplicationStatus.REJECTED
                && application.getStatus() == ApplicationStatus.REFERRED) {
            return;
        }
        throw new AccessDeniedException("Status transition is not allowed for this user");
    }
}
