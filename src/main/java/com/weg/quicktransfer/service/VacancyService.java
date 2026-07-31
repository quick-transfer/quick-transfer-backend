package com.weg.quicktransfer.service;

import java.util.List;
import java.util.UUID;

import com.weg.quicktransfer.dto.vacancy.VacancyFilter;
import com.weg.quicktransfer.repo.specifications.VacancySpecification;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
import com.weg.quicktransfer.exception.PlaceNotFoundException;
import com.weg.quicktransfer.exception.VacancyNotFoundException;
import com.weg.quicktransfer.exception.VacancySkillNotFoundException;
import com.weg.quicktransfer.mapper.VacancyMapper;
import com.weg.quicktransfer.model.Place;
import com.weg.quicktransfer.model.Vacancy;
import com.weg.quicktransfer.model.VacancySkill;
import com.weg.quicktransfer.repo.PlaceRepository;
import com.weg.quicktransfer.repo.VacancyRepository;
import com.weg.quicktransfer.repo.VacancySkillRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final PlaceRepository placeRepository;
    private final VacancySkillRepository vacancySkillRepository;

    @CacheEvict(value = "vacancies", allEntries = true)
    @Transactional
    public VacancyResponseDTO create(VacancyRequestDTO vacancyRequestDTO) {
        Place place = placeRepository.findById(vacancyRequestDTO.placeId()).orElseThrow(() -> new PlaceNotFoundException(vacancyRequestDTO.placeId()));
        List<VacancySkill> skills = resolveSkills(vacancyRequestDTO.skillIds());

        Vacancy vacancy = vacancyMapper.toEntity(vacancyRequestDTO, place, skills);

        vacancy = vacancyRepository.save(vacancy);

        return vacancyMapper.toResponse(vacancy);
    }

    @Cacheable("vacancies")
    @Transactional(readOnly = true)
    public List<VacancyResponseDTO> findAll() {
        List<Vacancy> vacancies = vacancyRepository.findAll();

        return vacancies.stream().map(vacancyMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<VacancyResponseDTO> findAll(Pageable pageable) {
        return vacancyRepository.findAll(pageable).map(vacancyMapper::toResponse);
    }

    @Cacheable(value = "vacancyById", key = "#id")
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

    @Caching(
            put = {
                    @CachePut(value = "vacancyById", key = "#id")
            },
            evict = {
                    @CacheEvict(value = "vacancies", allEntries = true)
            }
    )
    @Transactional
    public VacancyResponseDTO update(UUID id, VacancyUpdateRequestDTO vacancyUpdateRequestDTO) {
        Vacancy vacancy = vacancyRepository.findById(id).orElseThrow(() -> new VacancyNotFoundException(id));

        if(vacancyUpdateRequestDTO.name() != null && !vacancyUpdateRequestDTO.name().isBlank()) {
            vacancy.setName(vacancyUpdateRequestDTO.name());
        }

        if(vacancyUpdateRequestDTO.description() != null && !vacancyUpdateRequestDTO.description().isBlank()) {
            vacancy.setDescription(vacancyUpdateRequestDTO.description());
        }

        if(vacancyUpdateRequestDTO.area() != null && !vacancyUpdateRequestDTO.area().isBlank()) {
            vacancy.setArea(Area.valueOf(vacancyUpdateRequestDTO.area()));
        }

        if(vacancyUpdateRequestDTO.shift() != null && !vacancyUpdateRequestDTO.shift().isBlank()) {
            vacancy.setShift(Shift.valueOf(vacancyUpdateRequestDTO.shift()));
        }

        if(vacancyUpdateRequestDTO.placeId() != null) {
            Place place = placeRepository.findById(vacancyUpdateRequestDTO.placeId()).orElseThrow(() -> new PlaceNotFoundException(vacancyUpdateRequestDTO.placeId()));
            vacancy.setPlace(place);
        }

        if (vacancyUpdateRequestDTO.skillIds() != null) {
            vacancy.setSkills(resolveSkills(vacancyUpdateRequestDTO.skillIds()));
        }

        Vacancy vacancyAtt = vacancyRepository.save(vacancy);

        return vacancyMapper.toResponse(vacancyAtt);
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "vacancies", allEntries = true),
                    @CacheEvict(value = "vacancyById", key = "#id")
            }
    )
    @Transactional
    public void delete(UUID id) {
        if(!vacancyRepository.existsById(id)) {
            throw new VacancyNotFoundException(id);
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
}
