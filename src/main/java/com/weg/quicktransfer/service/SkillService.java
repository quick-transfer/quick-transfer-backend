package com.weg.quicktransfer.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.weg.quicktransfer.dto.skill.SkillRequestDTO;
import com.weg.quicktransfer.dto.skill.SkillResponseDTO;
import com.weg.quicktransfer.exception.SkillNotFoundException;
import com.weg.quicktransfer.exception.StudentNotFoundException;
import com.weg.quicktransfer.mapper.SkillMapper;
import com.weg.quicktransfer.model.Skill;
import com.weg.quicktransfer.model.Student;
import com.weg.quicktransfer.repo.SkillRepository;
import com.weg.quicktransfer.repo.StudentRepository;

import lombok.RequiredArgsConstructor;

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

    public void delete(Long id) {
        if(!skillRepository.existsById(id)) {
            throw new SkillNotFoundException(id);
        }

        skillRepository.deleteById(id);
    }
}
