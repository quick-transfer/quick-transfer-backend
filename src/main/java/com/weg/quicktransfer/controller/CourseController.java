package com.weg.quicktransfer.controller;

import com.weg.quicktransfer.dto.course.CourseFilter;
import com.weg.quicktransfer.dto.course.CourseRequestDTO;
import com.weg.quicktransfer.dto.course.CourseResponseDTO;
import com.weg.quicktransfer.dto.course.CourseUpdateRequestDTO;
import com.weg.quicktransfer.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/course")
public class CourseController {

    private final CourseService courseService;

    @PreAuthorize("hasRole('COORDINATOR')")
    @PostMapping("/create")
    public ResponseEntity<CourseResponseDTO> createCourse(@RequestBody @Valid CourseRequestDTO courseRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.create(courseRequestDTO));
    }

    @PreAuthorize("hasAnyRole('COORDINATOR', 'MANAGER')")
    @GetMapping("/find/id/{id}")
    public ResponseEntity<CourseResponseDTO> findCourseById(@PathVariable UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(courseService.findById(id));
    }

    @PreAuthorize("hasAnyRole('COORDINATOR', 'MANAGER')")
    @GetMapping("/find/name/{name}")
    public ResponseEntity<List<CourseResponseDTO>> findCourseByName(@PathVariable String name){
        return ResponseEntity.status(HttpStatus.OK).body(courseService.findByName(name));
    }

    @PreAuthorize("hasAnyRole('COORDINATOR', 'MANAGER')")
    @GetMapping("/find/all")
    public ResponseEntity<List<CourseResponseDTO>> findAllCourses(){
        return ResponseEntity.status(HttpStatus.OK).body(courseService.findAll());
    }

    @PreAuthorize("hasAnyRole('COORDINATOR', 'MANAGER')")
    @GetMapping("/search")
    public ResponseEntity<List<CourseResponseDTO>> searchCourses(CourseFilter filter) {
        return ResponseEntity.status(HttpStatus.OK).body(courseService.searchCourses(filter));
    }

    @PreAuthorize("hasRole('COORDINATOR')")
    @PatchMapping("/update/{id}")
    public ResponseEntity<CourseResponseDTO> updateCourse(
            @PathVariable UUID id,
            @RequestBody @Valid CourseUpdateRequestDTO courseUpdateRequestDTO
            ){
        return ResponseEntity.status(HttpStatus.OK).body(courseService.update(id, courseUpdateRequestDTO));
    }

    @PreAuthorize("hasRole('COORDINATOR')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable UUID id){
        courseService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
