package com.weg.quicktransfer.service;

import org.springframework.stereotype.Service;

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

    public StudentResponseDTO create(StudentRequestDTO studentRequestDTO) {
        

        Student student = studentMapper.toEntity(studentRequestDTO, null);

        
    }
}
