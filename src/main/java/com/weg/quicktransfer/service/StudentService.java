package com.weg.quicktransfer.service;

import java.util.List;
import java.util.UUID;

import com.weg.quicktransfer.dto.student.StudentFilter;
import com.weg.quicktransfer.repo.specifications.StudentSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
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

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final ClassEntityRepository classEntityRepository;

    @Transactional
    public StudentResponseDTO create(StudentRequestDTO studentRequestDTO) {
        ClassEntity classEntity = classEntityRepository.findById(studentRequestDTO.classId()).orElseThrow(() -> new ClassEntityNotFoundException(studentRequestDTO.classId()));

        Student student = studentMapper.toEntity(studentRequestDTO, classEntity);

        studentRepository.save(student);

        return studentMapper.toResponse(student);
    }

    @Transactional(readOnly = true)
    public List<StudentResponseDTO> findAll() {
        List<Student> students = studentRepository.findAll();

        return students.stream().map(studentMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public StudentResponseDTO findById(UUID id) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));

        return studentMapper.toResponse(student);
    }

    @Transactional(readOnly = true)
    public List<StudentResponseDTO> findByName(String name) {
        List<Student> students = studentRepository.findFirstByName(name);

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
    
    @Transactional
    public StudentResponseDTO update(UUID id, StudentUpdateRequestDTO studentUpdateRequestDTO) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));

        ClassEntity classEntity = classEntityRepository.findById(studentUpdateRequestDTO.classId()).orElseThrow(() -> new ClassEntityNotFoundException(studentUpdateRequestDTO.classId()));

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

        student.setClassEntity(classEntity);

        if(studentUpdateRequestDTO.statusStudentInterview() != null) {
            student.setStatus(StudentInterviewStatus.valueOf(studentUpdateRequestDTO.statusStudentInterview()));
        }

        if(studentUpdateRequestDTO.hasSeenEmail() != null) {
            student.setHasSeenEmail(studentUpdateRequestDTO.hasSeenEmail());
        }

        if(studentUpdateRequestDTO.statusStudent() != null) {
            student.setStatusStudent(StatusStudent.valueOf(studentUpdateRequestDTO.statusStudent()));
        }

        Student studentAtt = studentRepository.save(student);

        return studentMapper.toResponse(studentAtt);
    }

    @Transactional
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
