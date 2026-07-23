package com.weg.quicktransfer.controller;

import com.weg.quicktransfer.dto.course.CourseRequestDTO;
import com.weg.quicktransfer.dto.course.CourseResponseDTO;
import com.weg.quicktransfer.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/course")
public class CourseController {

    private final CourseService courseService;

    @PostMapping("/create")
    public ResponseEntity<CourseResponseDTO> createCourse(@RequestBody CourseRequestDTO courseRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.create(courseRequestDTO));
    }

    @GetMapping("/find/id/{id}")
    public ResponseEntity<CourseResponseDTO> findCourseById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(courseService.findById(id));
    }

    @GetMapping("/find/name/{name}")
    public ResponseEntity<CourseResponseDTO> findCourseByName(@PathVariable String name){
        return ResponseEntity.status(HttpStatus.OK).body(courseService.findByName(name));
    }
}
