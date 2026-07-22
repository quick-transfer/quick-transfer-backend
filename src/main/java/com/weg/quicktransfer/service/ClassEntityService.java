package com.weg.quicktransfer.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.weg.quicktransfer.dto.classEntity.ClassEntityRequestDTO;
import com.weg.quicktransfer.dto.classEntity.ClassEntityResponseDTO;
import com.weg.quicktransfer.dto.classEntity.ClassEntityUpdateRequestDTO;
import com.weg.quicktransfer.exception.ClassEntityNotFoundException;
import com.weg.quicktransfer.exception.CourseNotFoundException;
import com.weg.quicktransfer.mapper.ClassEntityMapper;
import com.weg.quicktransfer.model.ClassEntity;
import com.weg.quicktransfer.model.Course;
import com.weg.quicktransfer.repo.ClassEntityRepository;
import com.weg.quicktransfer.repo.CourseRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ClassEntityService {
    private final ClassEntityRepository classEntityRepository;
    private final ClassEntityMapper classEntityMapper;
    private final CourseRepository courseRepository;

    @Transactional
    public ClassEntityResponseDTO create(ClassEntityRequestDTO classEntityRequestDTO) {
        Course course = courseRepository.findById(classEntityRequestDTO.courseId()).orElseThrow(() -> new CourseNotFoundException(classEntityRequestDTO.courseId()));

        ClassEntity classEntity = classEntityMapper.toEntity(classEntityRequestDTO, course);

        classEntityRepository.save(classEntity);

        return classEntityMapper.toResponse(classEntity);
    }

    @Transactional(readOnly = true)
    public List<ClassEntityResponseDTO> findAll() {
        List<ClassEntity> classEntities = classEntityRepository.findAll();

        return classEntities.stream().map(classEntityMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ClassEntityResponseDTO findById(Long id) {
        ClassEntity classEntity = classEntityRepository.findById(id).orElseThrow(() -> new ClassEntityNotFoundException(id));

        return classEntityMapper.toResponse(classEntity);
    }

    @Transactional
    public ClassEntityResponseDTO update(Long id, LocalDate startDate, LocalDate finishDate, String acronym) {
        ClassEntity classEntity = classEntityRepository.findById(id).orElseThrow(() -> new ClassEntityNotFoundException(id));

        Course course = courseRepository.findById(id).orElseThrow(() -> new CourseNotFoundException(id));

        if(startDate != null) {
            classEntity.setStartDate(startDate);
        }

        if(finishDate != null) {
            classEntity.setFinishDate(finishDate);
        }

        if(StringUtils.hasText(acronym)) {
            classEntity.setAcronym(acronym);
        }

        ClassEntity classEntityAtt = classEntityRepository.save(classEntity);

        return classEntityMapper.toResponse(classEntityAtt);
    }

    @Transactional
    public void delete(Long id) {
        if(!classEntityRepository.existsById(id)) {
            throw new ClassEntityNotFoundException(id);
        }

        classEntityRepository.deleteById(id);
    }
}
