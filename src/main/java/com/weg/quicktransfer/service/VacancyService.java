package com.weg.quicktransfer.service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.weg.quicktransfer.dto.vacancy.VacancyFilter;
import com.weg.quicktransfer.repo.specifications.VacancySpecification;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.weg.quicktransfer.dto.vacancy.VacancyRequestDTO;
import com.weg.quicktransfer.dto.vacancy.VacancyResponseDTO;
import com.weg.quicktransfer.dto.vacancy.VacancyUpdateRequestDTO;
import com.weg.quicktransfer.enums.Area;
import com.weg.quicktransfer.enums.Shift;
import com.weg.quicktransfer.enums.Role;
import com.weg.quicktransfer.enums.VacancyStatus;
import com.weg.quicktransfer.exception.ManagerNotFoundException;
import com.weg.quicktransfer.exception.PlaceNotFoundException;
import com.weg.quicktransfer.exception.VacancyNotFoundException;
import com.weg.quicktransfer.exception.VacancySkillNotFoundException;
import com.weg.quicktransfer.mapper.VacancyMapper;
import com.weg.quicktransfer.model.Place;
import com.weg.quicktransfer.model.Manager;
import com.weg.quicktransfer.model.Vacancy;
import com.weg.quicktransfer.model.VacancySkill;
import com.weg.quicktransfer.repo.PlaceRepository;
import com.weg.quicktransfer.repo.ManagerRepository;
import com.weg.quicktransfer.repo.VacancyRepository;
import com.weg.quicktransfer.repo.VacancySkillRepository;

import lombok.RequiredArgsConstructor;
import com.weg.quicktransfer.security.UserPrincipal;
import org.springframework.security.access.AccessDeniedException;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final PlaceRepository placeRepository;
    private final VacancySkillRepository vacancySkillRepository;
    private final ManagerRepository managerRepository;

    @Transactional
    public VacancyResponseDTO create(VacancyRequestDTO vacancyRequestDTO) {
        return create(vacancyRequestDTO, null);
    }

    @Transactional
    public VacancyResponseDTO create(VacancyRequestDTO vacancyRequestDTO, UserPrincipal principal) {
        Place place = placeRepository.findById(vacancyRequestDTO.placeId()).orElseThrow(() -> new PlaceNotFoundException(vacancyRequestDTO.placeId()));
        List<VacancySkill> skills = resolveSkills(vacancyRequestDTO.skillIds());
        Manager manager = resolveManager(vacancyRequestDTO.managerId(), principal);
        validateManagerSection(manager, place);

        Vacancy vacancy = manager == null
                ? vacancyMapper.toEntity(vacancyRequestDTO, place, skills)
                : vacancyMapper.toEntity(vacancyRequestDTO, place, skills, manager);

        vacancy = vacancyRepository.save(vacancy);

        return vacancyMapper.toResponse(vacancy);
    }

    @Transactional(readOnly = true)
    public List<VacancyResponseDTO> findAll() {
        List<Vacancy> vacancies = vacancyRepository.findAll();

        return vacancies.stream().map(vacancyMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<VacancyResponseDTO> findAll(Pageable pageable) {
        return vacancyRepository.findAll(pageable).map(vacancyMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<VacancyResponseDTO> findAll(Pageable pageable, UserPrincipal principal) {
        if (principal != null && principal.getRole() == Role.MANAGER) {
            return vacancyRepository.findAllByManagerId(principal.getId(), pageable)
                    .map(vacancyMapper::toResponse);
        }
        return findAll(pageable);
    }

    @Transactional(readOnly = true)
    public VacancyResponseDTO findById(UUID id) {
        Vacancy vacancy = vacancyRepository.findById(id).orElseThrow(() -> new VacancyNotFoundException(id));

        return vacancyMapper.toResponse(vacancy);
    }

    @Transactional(readOnly = true)
    public List<VacancyResponseDTO> findByName(String name) {
        List<Vacancy> vacancies = vacancyRepository.findByName(name);

        return vacancies.stream()
                .map(vacancyMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<VacancyResponseDTO> searchVacancies(VacancyFilter filter, Pageable pageable) {
        Specification<Vacancy> spec = VacancySpecification.getFilteredVacancies(filter);
        return vacancyRepository.findAll(spec, pageable).map(vacancyMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<VacancyResponseDTO> searchVacancies(VacancyFilter filter) {
        Specification<Vacancy> spec = VacancySpecification.getFilteredVacancies(filter);

        Sort sort = Sort.by(Sort.Direction.ASC, "name");

        List<Vacancy> vacancies = vacancyRepository.findAll(spec, sort);

        return vacancies.stream()
                .map(vacancyMapper::toResponse)
                .toList();
    }

    @Transactional
    public VacancyResponseDTO update(UUID id, VacancyUpdateRequestDTO vacancyUpdateRequestDTO) {
        return update(id, vacancyUpdateRequestDTO, null);
    }

    @Transactional
    public VacancyResponseDTO update(UUID id, VacancyUpdateRequestDTO vacancyUpdateRequestDTO,
            UserPrincipal principal) {
        Vacancy vacancy = vacancyRepository.findById(id).orElseThrow(() -> new VacancyNotFoundException(id));
        validateManagerOwnership(vacancy, principal);

        if(vacancyUpdateRequestDTO.name() != null && !vacancyUpdateRequestDTO.name().isBlank()) {
            vacancy.setName(vacancyUpdateRequestDTO.name());
        }

        if(vacancyUpdateRequestDTO.description() != null && !vacancyUpdateRequestDTO.description().isBlank()) {
            vacancy.setDescription(vacancyUpdateRequestDTO.description());
        }

        if(vacancyUpdateRequestDTO.area() != null && !vacancyUpdateRequestDTO.area().isBlank()) {
            vacancy.setArea(Area.valueOf(vacancyUpdateRequestDTO.area().trim().toUpperCase(Locale.ROOT)));
        }

        if(vacancyUpdateRequestDTO.shift() != null && !vacancyUpdateRequestDTO.shift().isBlank()) {
            vacancy.setShift(Shift.valueOf(vacancyUpdateRequestDTO.shift().trim().toUpperCase(Locale.ROOT)));
        }

        if (vacancyUpdateRequestDTO.numbersVacancies() != null) {
            vacancy.setNumbersVacancies(vacancyUpdateRequestDTO.numbersVacancies());
        }

        if(vacancyUpdateRequestDTO.placeId() != null) {
            Place place = placeRepository.findById(vacancyUpdateRequestDTO.placeId()).orElseThrow(() -> new PlaceNotFoundException(vacancyUpdateRequestDTO.placeId()));
            vacancy.setPlace(place);
        }

        if (vacancyUpdateRequestDTO.skillIds() != null) {
            vacancy.setSkills(resolveSkills(vacancyUpdateRequestDTO.skillIds()));
        }

        if (vacancyUpdateRequestDTO.status() != null
                && !vacancyUpdateRequestDTO.status().isBlank()) {
            vacancy.setStatus(VacancyStatus.valueOf(
                    vacancyUpdateRequestDTO.status().trim().toUpperCase(Locale.ROOT)));
        }

        if (vacancyUpdateRequestDTO.managerId() != null
                && (principal == null || principal.getRole() == Role.ADMIN)) {
            vacancy.setManager(managerRepository.findById(vacancyUpdateRequestDTO.managerId())
                    .orElseThrow(() -> new ManagerNotFoundException(
                            vacancyUpdateRequestDTO.managerId())));
        }

        validateManagerSection(vacancy.getManager(), vacancy.getPlace());

        Vacancy vacancyAtt = vacancyRepository.save(vacancy);

        return vacancyMapper.toResponse(vacancyAtt);
    }

    @Transactional
    public void delete(UUID id) {
        delete(id, null);
    }

    @Transactional
    public void delete(UUID id, UserPrincipal principal) {
        if(!vacancyRepository.existsById(id)) {
            throw new VacancyNotFoundException(id);
        }

        if (principal != null && principal.getRole() == Role.MANAGER) {
            Vacancy vacancy = vacancyRepository.findById(id)
                    .orElseThrow(() -> new VacancyNotFoundException(id));
            validateManagerOwnership(vacancy, principal);
        }

        vacancyRepository.deleteById(id);
    }

    private List<VacancySkill> resolveSkills(List<UUID> skillIds) {
        if (skillIds == null || skillIds.isEmpty()) {
            return List.of();
        }

        return skillIds.stream()
                .distinct()
                .map(skillId -> vacancySkillRepository.findById(skillId)
                        .orElseThrow(() -> new VacancySkillNotFoundException(skillId)))
                .toList();
    }

    private Manager resolveManager(UUID requestedManagerId, UserPrincipal principal) {
        UUID managerId = principal != null && principal.getRole() == Role.MANAGER
                ? principal.getId()
                : requestedManagerId;
        if (managerId == null) {
            return null;
        }
        return managerRepository.findById(managerId)
                .orElseThrow(() -> new ManagerNotFoundException(managerId));
    }

    private void validateManagerOwnership(Vacancy vacancy, UserPrincipal principal) {
        if (principal == null || principal.getRole() != Role.MANAGER) {
            return;
        }
        if (vacancy.getManager() == null
                || !principal.getId().equals(vacancy.getManager().getId())) {
            throw new AccessDeniedException("Manager cannot modify another manager's vacancy");
        }
    }

    private void validateManagerSection(Manager manager, Place place) {
        if (manager != null && manager.getSection() != place.getSection()) {
            throw new IllegalArgumentException("Manager and vacancy place must belong to the same section");
        }
    }
}
