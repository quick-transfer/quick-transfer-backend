package com.weg.quicktransfer.service;

import java.util.List;
import java.util.UUID;

import com.weg.quicktransfer.dto.skill.SkillFilter;
import com.weg.quicktransfer.repo.specifications.SkillSpecification;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.weg.quicktransfer.dto.skill.SkillRequestDTO;
import com.weg.quicktransfer.dto.skill.SkillResponseDTO;
import com.weg.quicktransfer.dto.skill.SkillUpdateRequestDTO;
import com.weg.quicktransfer.enums.SkillType;
import com.weg.quicktransfer.exception.SkillNotFoundException;
import com.weg.quicktransfer.exception.StudentNotFoundException;
import com.weg.quicktransfer.mapper.SkillMapper;
import com.weg.quicktransfer.model.Skill;
import com.weg.quicktransfer.model.Student;
import com.weg.quicktransfer.repo.SkillRepository;
import com.weg.quicktransfer.repo.StudentRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final StudentRepository studentRepository;

    @CacheEvict(value = "skills", allEntries = true)
    @Transactional
    public SkillResponseDTO create(SkillRequestDTO skillRequestDTO) {
        Student student = studentRepository.findById(skillRequestDTO.studentId()).orElseThrow(() -> new StudentNotFoundException(skillRequestDTO.studentId()));

        Skill skill = skillMapper.toEntity(skillRequestDTO, student);

        skill = skillRepository.save(skill);

        return skillMapper.toResponse(skill);
    }

    @Cacheable("skills")
    @Transactional(readOnly = true)
    public List<SkillResponseDTO> findAll() {
        List<Skill> skills = skillRepository.findAll();

        return skills.stream().map(skillMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<SkillResponseDTO> findAll(Pageable pageable) {
        return skillRepository.findAll(pageable).map(skillMapper::toResponse);
    }

    @Cacheable(value = "skillById", key = "#id")
    @Transactional(readOnly = true)
    public SkillResponseDTO findById(UUID id) {
        Skill skill = skillRepository.findById(id).orElseThrow(() -> new SkillNotFoundException(id));

        return skillMapper.toResponse(skill);
    }

    @Transactional(readOnly = true)
    public SkillResponseDTO findByName(String name) {
        Skill skill = skillRepository.findFirstByName(name)
                .orElseThrow(() -> new SkillNotFoundException("Skill not found with the name: " + name));

        return skillMapper.toResponse(skill);
    }

    @Transactional
    public List<SkillResponseDTO> searchSkills(SkillFilter filter) {
        Specification<Skill> spec = SkillSpecification.getFilteredSkills(filter);
        List<Skill> admins = skillRepository.findAll(spec);

        return admins.stream()
                .map(skillMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<SkillResponseDTO> searchSkills(SkillFilter filter, Pageable pageable) {
        Specification<Skill> spec = SkillSpecification.getFilteredSkills(filter);
        return skillRepository.findAll(spec, pageable).map(skillMapper::toResponse);
    }

    @Caching(
            put = {
                    @CachePut(value = "skillById", key = "#id")
            },
            evict = {
                    @CacheEvict(value = "skills", allEntries = true)
            }
    )
    @Transactional
    public SkillResponseDTO update(UUID id, SkillUpdateRequestDTO skillUpdateRequestDTO) {
        Skill skill = skillRepository.findById(id).orElseThrow(() -> new SkillNotFoundException(id));

        if(skillUpdateRequestDTO.name() != null && !skillUpdateRequestDTO.name().isBlank()) {
            skill.setName(skillUpdateRequestDTO.name());
        }

        if(skillUpdateRequestDTO.skillType() != null) {
            skill.setSkillType(SkillType.valueOf(skillUpdateRequestDTO.skillType()));
        }

        if(skillUpdateRequestDTO.grade() != null) {
            skill.setGrade(skillUpdateRequestDTO.grade());
        }

        if(skillUpdateRequestDTO.studentId() != null) {
            Student student = studentRepository.findById(skillUpdateRequestDTO.studentId()).orElseThrow(() -> new StudentNotFoundException(skillUpdateRequestDTO.studentId()));
            skill.setStudent(student);
        }

        skillRepository.save(skill);

        return skillMapper.toResponse(skill);
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "skills", allEntries = true),
                    @CacheEvict(value = "skillById", key = "#id")
            }
    )
    @Transactional
    public void delete(UUID id) {
        if(!skillRepository.existsById(id)) {
            throw new SkillNotFoundException(id);
        }

        skillRepository.deleteById(id);
    }
}
