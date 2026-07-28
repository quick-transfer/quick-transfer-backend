package com.weg.quicktransfer.controller;

import com.weg.quicktransfer.dto.course.CourseRequestDTO;
import com.weg.quicktransfer.dto.course.CourseResponseDTO;
import com.weg.quicktransfer.dto.course.CourseUpdateRequestDTO;
import com.weg.quicktransfer.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/course")
public class CourseController {

    private final CourseService courseService;

    @PostMapping("/create")
    public ResponseEntity<CourseResponseDTO> createCourse(@RequestBody @Valid CourseRequestDTO courseRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.create(courseRequestDTO));
    }

    @GetMapping("/find/id/{id}")
    public ResponseEntity<CourseResponseDTO> findCourseById(@PathVariable UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(courseService.findById(id));
    }

    @GetMapping("/find/name/{name}")
    public ResponseEntity<CourseResponseDTO> findCourseByName(@PathVariable String name){
        return ResponseEntity.status(HttpStatus.OK).body(courseService.findByName(name));
    }

    @GetMapping("/find/all")
    public ResponseEntity<List<CourseResponseDTO>> findAllCourses(){
        return ResponseEntity.status(HttpStatus.OK).body(courseService.findAll());
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<CourseResponseDTO> updateCourse(
            @PathVariable UUID id,
            @RequestBody @Valid CourseUpdateRequestDTO courseUpdateRequestDTO
            ){
        return ResponseEntity.status(HttpStatus.OK).body(courseService.update(id, courseUpdateRequestDTO));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCourse(UUID id){
        courseService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
