package com.weg.quicktransfer.controller;

import com.weg.quicktransfer.dto.student.StudentFilter;
import com.weg.quicktransfer.dto.student.StudentRequestDTO;
import com.weg.quicktransfer.dto.student.StudentResponseDTO;
import com.weg.quicktransfer.dto.student.StudentUpdateRequestDTO;
import com.weg.quicktransfer.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PreAuthorize("hasRole('COORDINATOR')")
    @PostMapping("/create")
    public ResponseEntity<StudentResponseDTO> createStudent(@RequestBody @Valid StudentRequestDTO studentRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.create(studentRequestDTO));
    }

    @PreAuthorize("hasAnyRole('COORDINATOR', 'MANAGER')")
    @GetMapping("/find/id/{id}")
    public ResponseEntity<StudentResponseDTO> findStudentById(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(studentService.findById(id));
    }

    @PreAuthorize("hasAnyRole('COORDINATOR', 'MANAGER')")
    @GetMapping("/find/name/{name}")
    public ResponseEntity<List<StudentResponseDTO>> findCStudentByName(@PathVariable String name) {
        return ResponseEntity.status(HttpStatus.OK).body(studentService.findByName(name));
    }

    @PreAuthorize("hasAnyRole('COORDINATOR', 'MANAGER')")
    @GetMapping("/find/all")
    public ResponseEntity<Page<StudentResponseDTO>> findAllStudents(Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(studentService.findAll(pageable));
    }

    @PreAuthorize("hasAnyRole('COORDINATOR', 'MANAGER')")
    @GetMapping("/search")
    public ResponseEntity<Page<StudentResponseDTO>> searchCourses(StudentFilter filter, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(studentService.searchStudents(filter, pageable));
    }


    @PreAuthorize("hasRole('COORDINATOR')")
    @PatchMapping("/update/{id}")
    public ResponseEntity<StudentResponseDTO> updateStudent(
            @PathVariable UUID id,
            @RequestBody @Valid StudentUpdateRequestDTO updateRequestDTO
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(studentService.update(id, updateRequestDTO));
    }

    @PreAuthorize("hasRole('COORDINATOR')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable UUID id) {
        studentService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
