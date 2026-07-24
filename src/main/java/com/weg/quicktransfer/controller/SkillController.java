package com.weg.quicktransfer.controller;

import com.weg.quicktransfer.dto.skill.SkillRequestDTO;
import com.weg.quicktransfer.dto.skill.SkillResponseDTO;
import com.weg.quicktransfer.dto.skill.SkillUpdateRequestDTO;
import com.weg.quicktransfer.service.SkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/skill")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @PostMapping("/create")
    public ResponseEntity<SkillResponseDTO> createSkill(@RequestBody @Valid SkillRequestDTO skillRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(skillService.create(skillRequestDTO));
    }

    @GetMapping("/find/id/{id}")
    public ResponseEntity<SkillResponseDTO> findSkillById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(skillService.findById(id));
    }

    //@GetMapping("/find/name/{name}")
    //public ResponseEntity<SkillResponseDTO> findSkillByName(@PathVariable String name) {
    //    return ResponseEntity.status(HttpStatus.OK).body(skillService.findByName(name));
    //}

    @GetMapping("/find/all")
    public ResponseEntity<List<SkillResponseDTO>> findAllSkills() {
        return ResponseEntity.status(HttpStatus.OK).body(skillService.findAll());
    }

    //@PatchMapping("/update/{id}")
    //public ResponseEntity<SkillResponseDTO> updateSkill(
    //        @PathVariable Long id,
    //        @RequestBody @Valid SkillUpdateRequestDTO skillUpdateRequestDTO
    //) {
    //    return ResponseEntity.status(HttpStatus.OK).body(skillService.update(id, skillUpdateRequestDTO));
    //}

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long id) {
        skillService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
