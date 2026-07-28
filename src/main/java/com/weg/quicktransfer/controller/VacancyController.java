package com.weg.quicktransfer.controller;

import com.weg.quicktransfer.dto.interview.InterviewFilter;
import com.weg.quicktransfer.dto.interview.InterviewResponseDTO;
import com.weg.quicktransfer.dto.vacancy.VacancyFilter;
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
    public ResponseEntity<VacancyResponseDTO> findVacancyById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(vacancyService.findById(id));
    }

    @GetMapping("/find/name/{name}")
    public ResponseEntity<VacancyResponseDTO> findVacancyByName(@PathVariable String name) {
        return ResponseEntity.status(HttpStatus.OK).body(vacancyService.findByName(name));
    }

    @GetMapping("/find/all")
    public ResponseEntity<List<VacancyResponseDTO>> findAllVacancies() {
        return ResponseEntity.status(HttpStatus.OK).body(vacancyService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<VacancyResponseDTO>> searchVacancies(VacancyFilter filter) {
        return ResponseEntity.status(HttpStatus.OK).body(vacancyService.searchVacancies(filter));
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<VacancyResponseDTO> updateVacancy(
            @PathVariable Long id,
            @RequestBody @Valid VacancyUpdateRequestDTO vacancyUpdateRequestDTO
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(vacancyService.update(id, vacancyUpdateRequestDTO));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteVacancy(@PathVariable Long id) {
        vacancyService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
