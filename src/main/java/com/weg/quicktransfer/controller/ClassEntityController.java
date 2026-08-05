package com.weg.quicktransfer.controller;

import com.weg.quicktransfer.dto.classEntity.ClassEntityFilter;
import com.weg.quicktransfer.dto.classEntity.ClassEntityRequestDTO;
import com.weg.quicktransfer.dto.classEntity.ClassEntityResponseDTO;
import com.weg.quicktransfer.dto.classEntity.ClassEntityUpdateRequestDTO;
import com.weg.quicktransfer.service.ClassEntityService;

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
@RequestMapping("/class")
@RequiredArgsConstructor
public class ClassEntityController {

    private final ClassEntityService classEntityService;

    @PreAuthorize("hasRole('COORDINATOR')")
    @PostMapping("/create")
    public ResponseEntity<ClassEntityResponseDTO> createClassEntity(@RequestBody @Valid ClassEntityRequestDTO classEntityRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(classEntityService.create(classEntityRequestDTO));
    }

    @PreAuthorize("hasAnyRole('COORDINATOR', 'MANAGER')")
    @GetMapping("/find/id/{id}")
    public ResponseEntity<ClassEntityResponseDTO> findClassEntityById(@PathVariable UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(classEntityService.findById(id));
    }

    @PreAuthorize("hasAnyRole('COORDINATOR', 'MANAGER')")
    @GetMapping("find/acronym/{acronym}")
    public ResponseEntity<List<ClassEntityResponseDTO>> findClassEntityByAcronym(@PathVariable String acronym){
        return ResponseEntity.status(HttpStatus.OK).body(classEntityService.findByAcronym(acronym));
    }

    @PreAuthorize("hasAnyRole('COORDINATOR', 'MANAGER')")
    @GetMapping("/search")
    public ResponseEntity<Page<ClassEntityResponseDTO>> searchClassEntities(ClassEntityFilter filter, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(classEntityService.searchClassEntities(filter, pageable));
    }

    @PreAuthorize("hasAnyRole('COORDINATOR', 'MANAGER')")
    @GetMapping("find/all")
    public ResponseEntity<Page<ClassEntityResponseDTO>> findAllClassEntities(Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(classEntityService.findAll(pageable));
    }

    @PreAuthorize("hasRole('COORDINATOR')")
    @PatchMapping("/update/{id}")
    public ResponseEntity<ClassEntityResponseDTO> updateClassEntity(
            @PathVariable UUID id,
            @RequestBody @Valid ClassEntityUpdateRequestDTO classEntityUpdateRequestDTO){
        return ResponseEntity.status(HttpStatus.OK).body(classEntityService.update(id, classEntityUpdateRequestDTO));
    }

    @PreAuthorize("hasRole('COORDINATOR')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteClassEntity(@PathVariable UUID id){
        classEntityService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

