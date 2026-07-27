package com.weg.quicktransfer.controller;

import com.weg.quicktransfer.dto.admin.AdminFilter;
import com.weg.quicktransfer.dto.classEntity.ClassEntityFilter;
import com.weg.quicktransfer.dto.classEntity.ClassEntityRequestDTO;
import com.weg.quicktransfer.dto.classEntity.ClassEntityResponseDTO;
import com.weg.quicktransfer.dto.classEntity.ClassEntityUpdateRequestDTO;
import com.weg.quicktransfer.model.Admin;
import com.weg.quicktransfer.model.ClassEntity;
import com.weg.quicktransfer.service.ClassEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/search")
    public ResponseEntity<List<ClassEntity>> searchClassEntities(ClassEntityFilter filter) {
        return ResponseEntity.status(HttpStatus.OK).body(classEntityService.searchClassEntities(filter));
    }

    @GetMapping("find/all")
    public ResponseEntity<List<ClassEntityResponseDTO>> findAllClassEntities(){
        return ResponseEntity.status(HttpStatus.OK).body(classEntityService.findAll());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ClassEntityResponseDTO> updateClassEntity(
            @PathVariable Long id,
            @RequestBody ClassEntityUpdateRequestDTO classEntityUpdateRequestDTO){
        return ResponseEntity.status(HttpStatus.OK).body(classEntityService.update(id, classEntityUpdateRequestDTO));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteClassEntity(Long id){
        classEntityService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

