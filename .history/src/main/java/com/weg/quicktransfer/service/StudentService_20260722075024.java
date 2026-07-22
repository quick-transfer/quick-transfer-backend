package com.weg.quicktransfer.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.weg.quicktransfer.dto.student.StudentRequestDTO;
import com.weg.quicktransfer.dto.student.StudentResponseDTO;
import com.weg.quicktransfer.mapper.StudentMapper;
import com.weg.quicktransfer.model.Student;
import com.weg.quicktransfer.repo.StudentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    @Transactional
    public StudentResponseDTO create(StudentRequestDTO studentRequestDTO) {
        

        Student student = studentMapper.toEntity(studentRequestDTO, null);

        studentRepository.save(student);

        return studentMapper.toResponse(student);
    }

    public List<StudentResponseDTO> findAll() {
        List<Student> students = studentRepository.findAll();

        return students.stream().map(studentMapper::toResponse).toList();
    }

    public StudentResponseDTO findById()
}
