package com.weg.quicktransfer.service;

import java.util.List;
import java.util.UUID;

import com.weg.quicktransfer.dto.classEntity.ClassEntityFilter;
import com.weg.quicktransfer.repo.specifications.ClassEntitySpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.weg.quicktransfer.dto.classEntity.ClassEntityRequestDTO;
import com.weg.quicktransfer.dto.classEntity.ClassEntityResponseDTO;
import com.weg.quicktransfer.dto.classEntity.ClassEntityUpdateRequestDTO;
import com.weg.quicktransfer.enums.ShiftClass;
import com.weg.quicktransfer.enums.StatusClass;
import com.weg.quicktransfer.exception.ClassEntityNotFoundException;
import com.weg.quicktransfer.exception.CourseNotFoundException;
import com.weg.quicktransfer.exception.DateOutOfRangeException;
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
        validateDates(classEntityRequestDTO.startDate(), classEntityRequestDTO.finishDate());
        Course course = courseRepository.findById(classEntityRequestDTO.courseId()).orElseThrow(() -> new CourseNotFoundException(classEntityRequestDTO.courseId()));

        ClassEntity classEntity = classEntityMapper.toEntity(classEntityRequestDTO, course);

        classEntity = classEntityRepository.save(classEntity);

        return classEntityMapper.toResponse(classEntity);
    }

    @Transactional(readOnly = true)
    public List<ClassEntityResponseDTO> findAll() {
        List<ClassEntity> classEntities = classEntityRepository.findAll();

        return classEntities.stream().map(classEntityMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<ClassEntityResponseDTO> findAll(Pageable pageable) {
        return classEntityRepository.findAll(pageable).map(classEntityMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<ClassEntityResponseDTO> findByAcronym(String acronym){
        if(!StringUtils.hasText(acronym)){
            throw new IllegalArgumentException("Acronym can not be empty");
        }

        List<ClassEntity> classEntities = classEntityRepository.findByAcronym(acronym);

        return classEntities.stream()
                .map(classEntityMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ClassEntityResponseDTO> searchClassEntities(ClassEntityFilter filter, Pageable pageable) {
        Specification<ClassEntity> spec = ClassEntitySpecification.getFilteredClassEntities(filter);
        return classEntityRepository.findAll(spec, pageable).map(classEntityMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ClassEntityResponseDTO findById(UUID id) {
        ClassEntity classEntity = classEntityRepository.findById(id).orElseThrow(() -> new ClassEntityNotFoundException(id));

        return classEntityMapper.toResponse(classEntity);
    }

    @Transactional
    public List<ClassEntityResponseDTO> searchClassEntities(ClassEntityFilter filter) {
        Specification<ClassEntity> spec = ClassEntitySpecification.getFilteredClassEntities(filter);
        List<ClassEntity> classEntities = classEntityRepository.findAll(spec);

        return classEntities.stream()
                .map(classEntityMapper::toResponse)
                .toList();
    }

    @Transactional
    public ClassEntityResponseDTO update(UUID id, ClassEntityUpdateRequestDTO classEntityUpdateRequestDTO) {
        ClassEntity classEntity = classEntityRepository.findById(id).orElseThrow(() -> new ClassEntityNotFoundException(id));
        
        if(classEntityUpdateRequestDTO.courseId() != null) {
            Course course = courseRepository.findById(classEntityUpdateRequestDTO.courseId()).orElseThrow(() -> new CourseNotFoundException(classEntityUpdateRequestDTO.courseId()));
            classEntity.setCourse(course);
        }

        if(classEntityUpdateRequestDTO.startDate() != null) {
            classEntity.setStartDate(classEntityUpdateRequestDTO.startDate());
        }

        if(classEntityUpdateRequestDTO.finishDate() != null) {
            classEntity.setFinishDate(classEntityUpdateRequestDTO.finishDate());
        }

        if(classEntityUpdateRequestDTO.status() != null) {
            classEntity.setStatus(StatusClass.valueOf(classEntityUpdateRequestDTO.status()));
        }

        if(classEntityUpdateRequestDTO.shiftClass() != null) {
            classEntity.setShiftClass(ShiftClass.valueOf(classEntityUpdateRequestDTO.shiftClass()));
        }

        if(classEntityUpdateRequestDTO.acronym() != null && !classEntityUpdateRequestDTO.acronym().isBlank()) {
            classEntity.setAcronym(classEntityUpdateRequestDTO.acronym());
        }

        validateDates(classEntity.getStartDate(), classEntity.getFinishDate());

        ClassEntity classEntityAtt = classEntityRepository.save(classEntity);

        return classEntityMapper.toResponse(classEntityAtt);
    }

    @Transactional
    public void delete(UUID id) {
        if(!classEntityRepository.existsById(id)) {
            throw new ClassEntityNotFoundException(id);
        }

        classEntityRepository.deleteById(id);
    }

    private void validateDates(java.time.LocalDate startDate, java.time.LocalDate finishDate) {
        if (startDate != null && finishDate != null && finishDate.isBefore(startDate)) {
            throw new DateOutOfRangeException("Finish date cannot be before start date");
        }
    }
}
