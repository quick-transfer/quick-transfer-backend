package com.weg.quicktransfer.controller;

import com.weg.quicktransfer.dto.student.StudentRequestDTO;
import com.weg.quicktransfer.dto.student.StudentResponseDTO;
import com.weg.quicktransfer.dto.student.StudentUpdateRequestDTO;
import com.weg.quicktransfer.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping("/create")
    public ResponseEntity<StudentResponseDTO> createStudent(@RequestBody @Valid StudentRequestDTO studentRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.create(studentRequestDTO));
    }

    @GetMapping("/find/id/{id}")
    public ResponseEntity<StudentResponseDTO> findStudentById(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(studentService.findById(id));
    }

    @GetMapping("/find/name/{name}")
    public ResponseEntity<List<StudentResponseDTO>> findCStudentByName(@PathVariable String name) {
        return ResponseEntity.status(HttpStatus.OK).body(studentService.findByName(name));
    }

    @GetMapping("/find/all")
    public ResponseEntity<List<StudentResponseDTO>> findAllStudents() {
        return ResponseEntity.status(HttpStatus.OK).body(studentService.findAll());
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<StudentResponseDTO> updateStudent(
            @PathVariable UUID id,
            @RequestBody @Valid StudentUpdateRequestDTO updateRequestDTO
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(studentService.update(id, updateRequestDTO));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable UUID id) {
        studentService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
