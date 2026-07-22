package com.weg.quicktransfer.service;

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
    public ClassEntityResponseDTO update(Long id, ClassEntityUpdateRequestDTO classEntityUpdateRequestDTO) {
        ClassEntity classEntity = classEntityRepository.findById(id).orElseThrow(() -> new ClassEntityNotFoundException(id));

        Course course = courseRepository.findById(classEntityUpdateRequestDTO.courseId()).orElseThrow(() -> new CourseNotFoundException(classEntityUpdateRequestDTO.courseId()));

        if(classEntityUpdateRequestDTO.courseId() != null) {
            classEntity.setCourse(course);
        }

        if(classEntityUpdateRequestDTO.startDate() != null) {
            classEntity.setStartDate(classEntityUpdateRequestDTO.startDate());
        }

        if(classEntityUpdateRequestDTO.finishDate() != null) {
            classEntity.setFinishDate(classEntityUpdateRequestDTO.finishDate());
        }

        if(classEntityUpdateRequestDTO.acronym() != null && classEntityUpdateRequestDTO.acronym().isBlank()) {
            classEntity.setAcronym(classEntityUpdateRequestDTO.acronym());
        }

        ClassEntity classEntityAtt = classEntityRepository.save(classEntity);

        return classEntityMapper.toResponse(classEntityAtt);
    }

    @Transactional
    public void delete(Long id) {
        if(classEntityRepository.existsById(id)) {
            throw new ClassEntityNotFoundException(id);
        }

        classEntityRepository.deleteById(id);
    }
}
