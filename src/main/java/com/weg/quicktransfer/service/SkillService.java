package com.weg.quicktransfer.service;

import java.util.List;
import java.util.UUID;

import com.weg.quicktransfer.dto.admin.AdminFilter;
import com.weg.quicktransfer.dto.admin.AdminResponseDTO;
import com.weg.quicktransfer.dto.skill.SkillFilter;
import com.weg.quicktransfer.model.Admin;
import com.weg.quicktransfer.repo.specifications.AdminSpecification;
import com.weg.quicktransfer.repo.specifications.InterviewSpecification;
import com.weg.quicktransfer.repo.specifications.SkillSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Transactional
    public SkillResponseDTO create(SkillRequestDTO skillRequestDTO) {
        Student student = studentRepository.findById(skillRequestDTO.studentId()).orElseThrow(() -> new StudentNotFoundException(skillRequestDTO.studentId()));

        Skill skill = skillMapper.toEntity(skillRequestDTO, student);

        skillRepository.save(skill);

        return skillMapper.toResponse(skill);
    }

    @Transactional(readOnly = true)
    public List<SkillResponseDTO> findAll() {
        List<Skill> skills = skillRepository.findAll();

        return skills.stream().map(skillMapper::toResponse).toList();
    }

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

    @Transactional
    public void delete(UUID id) {
        if(!skillRepository.existsById(id)) {
            throw new SkillNotFoundException(id);
        }

        skillRepository.deleteById(id);
    }
}
