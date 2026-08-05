package com.weg.quicktransfer.service;

import com.weg.quicktransfer.dto.vacancy.VacancySkillFilter;
import com.weg.quicktransfer.dto.vacancy.VacancySkillRequestDTO;
import com.weg.quicktransfer.dto.vacancy.VacancySkillResponseDTO;
import com.weg.quicktransfer.dto.vacancy.VacancySkillUpdateRequestDTO;
import com.weg.quicktransfer.enums.SkillType;
import com.weg.quicktransfer.exception.VacancySkillNotFoundException;
import com.weg.quicktransfer.mapper.VacancySkillMapper;
import com.weg.quicktransfer.model.Vacancy;
import com.weg.quicktransfer.model.VacancySkill;
import com.weg.quicktransfer.repo.VacancyRepository;
import com.weg.quicktransfer.repo.VacancySkillRepository;
import com.weg.quicktransfer.repo.specifications.VacancySkillSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VacancySkillService {

    private final VacancySkillRepository vacancySkillRepository;
    private final VacancyRepository vacancyRepository;
    private final VacancySkillMapper vacancySkillMapper;

    @CacheEvict(value = "vacancySkills", allEntries = true)
    @Transactional
    public VacancySkillResponseDTO create(VacancySkillRequestDTO requestDTO) {
        VacancySkill vacancySkill = vacancySkillMapper.toEntity(requestDTO);
        vacancySkill = vacancySkillRepository.save(vacancySkill);
        return vacancySkillMapper.toResponse(vacancySkill);
    }

    @Cacheable("vacancySkills")
    @Transactional(readOnly = true)
    public List<VacancySkillResponseDTO> findAll() {
        return vacancySkillRepository.findAll(Sort.by(Sort.Direction.ASC, "name")).stream()
                .map(vacancySkillMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<VacancySkillResponseDTO> findAll(Pageable pageable) {
        return vacancySkillRepository.findAll(pageable).map(vacancySkillMapper::toResponse);
    }

    @Cacheable(value = "vacancySkillById", key = "#id")
    @Transactional(readOnly = true)
    public VacancySkillResponseDTO findById(UUID id) {
        VacancySkill vacancySkill = vacancySkillRepository.findById(id)
                .orElseThrow(() -> new VacancySkillNotFoundException(id));
        return vacancySkillMapper.toResponse(vacancySkill);
    }

    @Transactional(readOnly = true)
    public VacancySkillResponseDTO findByName(String name) {
        VacancySkill vacancySkill = vacancySkillRepository.findFirstByNameIgnoreCase(name)
                .orElseThrow(() -> new VacancySkillNotFoundException(
                        "Vacancy skill not found with the name: " + name));
        return vacancySkillMapper.toResponse(vacancySkill);
    }

    @Transactional(readOnly = true)
    public List<VacancySkillResponseDTO> searchSkills(VacancySkillFilter filter) {
        Specification<VacancySkill> specification =
                VacancySkillSpecification.getFilteredSkills(filter);
        return vacancySkillRepository.findAll(
                        specification,
                        Sort.by(Sort.Direction.ASC, "name")
                ).stream()
                .map(vacancySkillMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<VacancySkillResponseDTO> searchSkills(
            VacancySkillFilter filter,
            Pageable pageable
    ) {
        Specification<VacancySkill> specification =
                VacancySkillSpecification.getFilteredSkills(filter);
        return vacancySkillRepository.findAll(specification, pageable)
                .map(vacancySkillMapper::toResponse);
    }

    @Caching(
            put = @CachePut(value = "vacancySkillById", key = "#id"),
            evict = {
                    @CacheEvict(value = "vacancySkills", allEntries = true),
                    @CacheEvict(value = "vacancies", allEntries = true),
                    @CacheEvict(value = "vacancyById", allEntries = true)
            }
    )
    @Transactional
    public VacancySkillResponseDTO update(UUID id, VacancySkillUpdateRequestDTO requestDTO) {
        VacancySkill vacancySkill = vacancySkillRepository.findById(id)
                .orElseThrow(() -> new VacancySkillNotFoundException(id));

        if (requestDTO.name() != null && !requestDTO.name().isBlank()) {
            vacancySkill.setName(requestDTO.name());
        }

        if (requestDTO.skillType() != null && !requestDTO.skillType().isBlank()) {
            vacancySkill.setSkillType(SkillType.valueOf(requestDTO.skillType()));
        }

        if (requestDTO.minimumGrade() != null) {
            vacancySkill.setMinimumGrade(requestDTO.minimumGrade());
        }

        vacancySkill = vacancySkillRepository.save(vacancySkill);
        return vacancySkillMapper.toResponse(vacancySkill);
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "vacancySkills", allEntries = true),
                    @CacheEvict(value = "vacancySkillById", key = "#id"),
                    @CacheEvict(value = "vacancies", allEntries = true),
                    @CacheEvict(value = "vacancyById", allEntries = true)
            }
    )
    @Transactional
    public void delete(UUID id) {
        VacancySkill vacancySkill = vacancySkillRepository.findById(id)
                .orElseThrow(() -> new VacancySkillNotFoundException(id));

        List<Vacancy> vacancies = new ArrayList<>(vacancySkill.getVacancies());
        vacancies.forEach(vacancy -> vacancy.removeSkill(vacancySkill));
        if (!vacancies.isEmpty()) {
            vacancyRepository.saveAll(vacancies);
        }

        vacancySkillRepository.delete(vacancySkill);
    }
}
