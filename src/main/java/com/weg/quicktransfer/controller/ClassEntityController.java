package com.weg.quicktransfer.controller;

import com.weg.quicktransfer.dto.classEntity.ClassEntityRequestDTO;
import com.weg.quicktransfer.dto.classEntity.ClassEntityResponseDTO;
import com.weg.quicktransfer.service.ClassEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/class")
@RequiredArgsConstructor
public class ClassEntityController {

    private final ClassEntityService classEntityService;

    @PostMapping("/create")
    public ResponseEntity<ClassEntityResponseDTO> createClassEntity(@RequestBody  ClassEntityRequestDTO classEntityRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(classEntityService.create(classEntityRequestDTO));
    }

    @GetMapping("/find/id/{id}")
    public ResponseEntity<ClassEntityResponseDTO> findClassEntityById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(classEntityService.findById(id));
    }

    @GetMapping("find/name/{name}")
    public ResponseEntity<ClassEntityResponseDTO> findClassEntityByAcronym(@PathVariable String name){
        return ResponseEntity.status(HttpStatus.OK).body(classEntityService.findByAcronym(name));
    }
}
