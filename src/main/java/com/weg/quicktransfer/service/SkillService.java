package com.weg.quicktransfer.service;

import java.util.List;

import com.weg.quicktransfer.dto.skill.SkillFilter;
import com.weg.quicktransfer.repo.specifications.InterviewSpecification;
import com.weg.quicktransfer.repo.specifications.SkillSpecification;
import org.springframework.data.jpa.domain.Specification;
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


    public SkillResponseDTO create(SkillRequestDTO skillRequestDTO) {
        Student student = studentRepository.findById(skillRequestDTO.studentId()).orElseThrow(() -> new StudentNotFoundException(skillRequestDTO.studentId()));

        Skill skill = skillMapper.toEntity(skillRequestDTO, student);

        skillRepository.save(skill);

        return skillMapper.toResponse(skill);
    }

    public List<SkillResponseDTO> findAll() {
        List<Skill> skills = skillRepository.findAll();

        return skills.stream().map(skillMapper::toResponse).toList();
    }

    public SkillResponseDTO findById(Long id) {
        Skill skill = skillRepository.findById(id).orElseThrow(() -> new SkillNotFoundException(id));

        return skillMapper.toResponse(skill);
    }

    @Transactional(readOnly = true)
    public List<SkillResponseDTO> searchSkills(SkillFilter filter) {
        Specification<Skill> spec = SkillSpecification.getFilteredSkills(filter);

        List<Skill> interviews = skillRepository.findAll(spec);

        return interviews.stream()
                .map(skillMapper::toResponse)
                .toList();
    }

    public SkillResponseDTO update(Long id, SkillUpdateRequestDTO skillUpdateRequestDTO) {
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

    public void delete(Long id) {
        if(!skillRepository.existsById(id)) {
            throw new SkillNotFoundException(id);
        }

        skillRepository.deleteById(id);
    }
}
