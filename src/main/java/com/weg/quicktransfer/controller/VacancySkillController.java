package com.weg.quicktransfer.controller;

import com.weg.quicktransfer.dto.vacancy.VacancySkillFilter;
import com.weg.quicktransfer.dto.vacancy.VacancySkillRequestDTO;
import com.weg.quicktransfer.dto.vacancy.VacancySkillResponseDTO;
import com.weg.quicktransfer.dto.vacancy.VacancySkillUpdateRequestDTO;
import com.weg.quicktransfer.service.VacancySkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/vacancy-skill")
@RequiredArgsConstructor
public class VacancySkillController {

    private final VacancySkillService vacancySkillService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("/create")
    public ResponseEntity<VacancySkillResponseDTO> create(
            @RequestBody @Valid VacancySkillRequestDTO requestDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vacancySkillService.create(requestDTO));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR', 'MANAGER')")
    @GetMapping("/find/id/{id}")
    public ResponseEntity<VacancySkillResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(vacancySkillService.findById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR', 'MANAGER')")
    @GetMapping("/find/name/{name}")
    public ResponseEntity<VacancySkillResponseDTO> findByName(@PathVariable String name) {
        return ResponseEntity.ok(vacancySkillService.findByName(name));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR', 'MANAGER')")
    @GetMapping("/find/all")
    public ResponseEntity<Page<VacancySkillResponseDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(vacancySkillService.findAll(pageable));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINATOR', 'MANAGER')")
    @GetMapping("/search")
    public ResponseEntity<Page<VacancySkillResponseDTO>> search(
            VacancySkillFilter filter,
            Pageable pageable
    ) {
        return ResponseEntity.ok(vacancySkillService.searchSkills(filter, pageable));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PatchMapping("/update/{id}")
    public ResponseEntity<VacancySkillResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody @Valid VacancySkillUpdateRequestDTO requestDTO
    ) {
        return ResponseEntity.ok(vacancySkillService.update(id, requestDTO));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        vacancySkillService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
