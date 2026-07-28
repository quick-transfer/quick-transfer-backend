package com.weg.quicktransfer.controller;

import com.weg.quicktransfer.dto.vacancy.VacancyRequestDTO;
import com.weg.quicktransfer.dto.vacancy.VacancyResponseDTO;
import com.weg.quicktransfer.dto.vacancy.VacancyUpdateRequestDTO;
import com.weg.quicktransfer.service.VacancyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/vacancy")
@RequiredArgsConstructor
public class VacancyController {

    private final VacancyService vacancyService;

    @PostMapping("/create")
    public ResponseEntity<VacancyResponseDTO> createVacancy(@RequestBody @Valid VacancyRequestDTO vacancyRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vacancyService.create(vacancyRequestDTO));
    }

    @GetMapping("/find/id/{id}")
    public ResponseEntity<VacancyResponseDTO> findVacancyById(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(vacancyService.findById(id));
    }

    @GetMapping("/find/name/{name}")
    public ResponseEntity<List<VacancyResponseDTO>> findVacancyByName(@PathVariable String name) {
        return ResponseEntity.status(HttpStatus.OK).body(vacancyService.findByName(name));
    }

    @GetMapping("/find/all")
    public ResponseEntity<List<VacancyResponseDTO>> findAllVacancys() {
        return ResponseEntity.status(HttpStatus.OK).body(vacancyService.findAll());
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<VacancyResponseDTO> updateVacancy(
            @PathVariable UUID id,
            @RequestBody @Valid VacancyUpdateRequestDTO vacancyUpdateRequestDTO
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(vacancyService.update(id, vacancyUpdateRequestDTO));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteVacancy(@PathVariable UUID id) {
        vacancyService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
