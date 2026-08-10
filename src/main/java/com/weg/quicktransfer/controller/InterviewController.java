package com.weg.quicktransfer.controller;

import com.weg.quicktransfer.dto.interview.InterviewFilter;
import com.weg.quicktransfer.dto.interview.InterviewRequestDTO;
import com.weg.quicktransfer.dto.interview.InterviewResponseDTO;
import com.weg.quicktransfer.dto.interview.InterviewUpdateRequestDTO;
import com.weg.quicktransfer.service.InterviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/interview")
public class InterviewController {

    private final InterviewService interviewService;

    @PreAuthorize("hasAnyRole('COORDINATOR', 'MANAGER' , 'ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<InterviewResponseDTO> createInterview(@RequestBody @Valid InterviewRequestDTO interviewRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(interviewService.create(interviewRequestDTO));
    }

    @PreAuthorize("hasAnyRole('COORDINATOR', 'MANAGER', 'ADMIN')")
    @GetMapping("/find/id/{id}")
    public ResponseEntity<InterviewResponseDTO> findInterviewById(@PathVariable UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(interviewService.findById(id));
    }

    @PreAuthorize("hasAnyRole('COORDINATOR', 'MANAGER', 'ADMIN')")
    @GetMapping("/find/all")
    public ResponseEntity<Page<InterviewResponseDTO>> findAllInterviews(Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(interviewService.findAll(pageable));
    }

    @PreAuthorize("hasAnyRole('COORDINATOR', 'MANAGER', 'ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<Page<InterviewResponseDTO>> searchCourses(InterviewFilter filter, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(interviewService.searchInterviews(filter, pageable));
    }

    @PreAuthorize("hasAnyRole('COORDINATOR', 'MANAGER', 'ADMIN')")
    @PatchMapping("/update/{id}")
    public ResponseEntity<InterviewResponseDTO> updateInterview(
            @PathVariable UUID id,
            @RequestBody @Valid InterviewUpdateRequestDTO interviewUpdateRequestDTO
    ){
        return ResponseEntity.status(HttpStatus.OK).body(interviewService.update(id, interviewUpdateRequestDTO));
    }

    @PreAuthorize("hasAnyRole(COORDINATOR', 'MANAGER', 'ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteInterview(@PathVariable UUID id){
        interviewService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
