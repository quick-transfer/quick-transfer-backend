package com.weg.quicktransfer.service;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.weg.quicktransfer.dto.student.StudentFilter;
import com.weg.quicktransfer.repo.specifications.StudentSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.transaction.annotation.Transactional;

import com.weg.quicktransfer.dto.student.StudentRequestDTO;
import com.weg.quicktransfer.dto.student.StudentResponseDTO;
import com.weg.quicktransfer.dto.student.StudentUpdateRequestDTO;
import com.weg.quicktransfer.enums.StatusStudent;
import com.weg.quicktransfer.enums.StudentInterviewStatus;
import com.weg.quicktransfer.exception.ClassEntityNotFoundException;
import com.weg.quicktransfer.exception.StudentNotFoundException;
import com.weg.quicktransfer.mapper.StudentMapper;
import com.weg.quicktransfer.model.ClassEntity;
import com.weg.quicktransfer.model.Student;
import com.weg.quicktransfer.repo.ClassEntityRepository;
import com.weg.quicktransfer.repo.StudentRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final ClassEntityRepository classEntityRepository;

    private final ObjectMapper objectMapper;

    @Transactional
    public StudentResponseDTO create(StudentRequestDTO studentRequestDTO) {
        ClassEntity classEntity = classEntityRepository.findById(studentRequestDTO.classId()).orElseThrow(() -> new ClassEntityNotFoundException(studentRequestDTO.classId()));

        Student student = studentMapper.toEntity(studentRequestDTO, classEntity);

        student = studentRepository.save(student);

        return studentMapper.toResponse(student);
    }

    @Transactional
    public List<StudentResponseDTO> createMultiple(MultipartFile file) throws IOException {
        List<StudentRequestDTO> studentsRequest = objectMapper.readValue(
                file.getInputStream(),
                new TypeReference<List<StudentRequestDTO>>() {
                }
        );

        List<Student> students = studentsRequest.stream()
                .map(dto -> studentMapper.toEntity(dto, classEntityRepository.findById(dto.classId())
                        .orElseThrow(() -> new ClassEntityNotFoundException("The operation was canceled because one of the classes id was invalid"))))
                .toList();

        return studentRepository.saveAll(students).stream()
                .map(studentMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StudentResponseDTO> findAll() {
        List<Student> students = studentRepository.findAll();

        return students.stream().map(studentMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<StudentResponseDTO> findAll(Pageable pageable) {
        return studentRepository.findAll(pageable).map(studentMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public StudentResponseDTO findById(UUID id) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));

        return studentMapper.toResponse(student);
    }

    @Transactional(readOnly = true)
    public List<StudentResponseDTO> findByName(String name) {
        List<Student> students = studentRepository.findByName(name);

        return students.stream()
                .map(studentMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StudentResponseDTO> searchInterviews(StudentFilter filter) {
        Specification<Student> spec = StudentSpecification.getFilteredStudents(filter);

        List<Student> interviews = studentRepository.findAll(spec);

        return interviews.stream()
                .map(studentMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<StudentResponseDTO> searchStudents(StudentFilter filter, Pageable pageable) {
        Specification<Student> spec = StudentSpecification.getFilteredStudents(filter);
        return studentRepository.findAll(spec, pageable).map(studentMapper::toResponse);
    }
    
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "skills", allEntries = true),
            @CacheEvict(value = "skillById", allEntries = true)
    })
    public StudentResponseDTO update(UUID id, StudentUpdateRequestDTO studentUpdateRequestDTO) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));

        

        if(studentUpdateRequestDTO.name() != null && !studentUpdateRequestDTO.name().isBlank()) {
            student.setName(studentUpdateRequestDTO.name());
        }

        if(studentUpdateRequestDTO.email() != null && !studentUpdateRequestDTO.email().isBlank()) {
            student.setEmail(studentUpdateRequestDTO.email());
        }

        if(studentUpdateRequestDTO.age() != null) {
            student.setAge(studentUpdateRequestDTO.age());
        }

        if(studentUpdateRequestDTO.averageGrade() != null) {
            student.setAverageGrade(studentUpdateRequestDTO.averageGrade());
        }

        if(studentUpdateRequestDTO.classId() != null) {
            ClassEntity classEntity = classEntityRepository.findById(studentUpdateRequestDTO.classId()).orElseThrow(() -> new ClassEntityNotFoundException(studentUpdateRequestDTO.classId()));
            student.setClassEntity(classEntity);
        }

        if(studentUpdateRequestDTO.statusStudentInterview() != null) {
            student.setStatus(StudentInterviewStatus.valueOf(studentUpdateRequestDTO.statusStudentInterview().trim().toUpperCase(Locale.ROOT)));
        }

        if(studentUpdateRequestDTO.hasSeenEmail() != null) {
            student.setHasSeenEmail(studentUpdateRequestDTO.hasSeenEmail());
        }

        if(studentUpdateRequestDTO.statusStudent() != null) {
            student.setStatusStudent(StatusStudent.valueOf(studentUpdateRequestDTO.statusStudent().trim().toUpperCase(Locale.ROOT)));
        }

        Student studentAtt = studentRepository.save(student);

        return studentMapper.toResponse(studentAtt);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "skills", allEntries = true),
            @CacheEvict(value = "skillById", allEntries = true)
    })
    public void delete(UUID id) {
        if(!studentRepository.existsById(id)) {
            throw new StudentNotFoundException(id);
        }

        studentRepository.deleteById(id);
    }

    @Transactional
    public StudentResponseDTO markEmailAsRead(UUID id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));

        student.setHasSeenEmail(true);

        Student savedStudent = studentRepository.save(student);

        return studentMapper.toResponse(savedStudent);
    }
}
