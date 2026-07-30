package com.weg.quicktransfer.controller;

import com.weg.quicktransfer.dto.manager.ManagerFilter;
import com.weg.quicktransfer.dto.manager.ManagerRequestDTO;
import com.weg.quicktransfer.dto.manager.ManagerResponseDTO;
import com.weg.quicktransfer.dto.manager.ManagerUpdateRequestDTO;
import com.weg.quicktransfer.service.ManagerService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/manager")
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<ManagerResponseDTO> createManager(@RequestBody @Valid ManagerRequestDTO managerRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(managerService.create(managerRequestDTO));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/find/id/{id}")
    public ResponseEntity<ManagerResponseDTO> findManagerById(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(managerService.findById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/find/name/{name}")
    public ResponseEntity<List<ManagerResponseDTO>> findManagerByName(@PathVariable String name) {
        return ResponseEntity.status(HttpStatus.OK).body(managerService.findByName(name));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/find/all")
    public ResponseEntity<List<ManagerResponseDTO>> findAllManagers() {
        return ResponseEntity.status(HttpStatus.OK).body(managerService.findAll());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/search")
    public ResponseEntity<List<ManagerResponseDTO>> searchManager(ManagerFilter filter) {
        return ResponseEntity.status(HttpStatus.OK).body(managerService.searchManagers(filter));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PatchMapping("/update/{id}")
    public ResponseEntity<ManagerResponseDTO> updateManager(
            @PathVariable UUID id,
            @RequestBody @Valid ManagerUpdateRequestDTO updateRequestDTO,
            @RequestParam UUID userId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(managerService.update(id, updateRequestDTO, userId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteManager(@PathVariable UUID id) {
        managerService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/interview/sendEmail/{interviewId}")
    public ResponseEntity<String> postSendInterviewEmail(
            @PathVariable UUID interviewId,
            @RequestParam("email") String to,
            @RequestHeader(value = "AMP-Same-Origin", required = false) String sameOrigin,
            @RequestHeader(value = "AMP-Email-Sender", required = false) String sender)  throws MessagingException {

        managerService.sendDynamicEmailAmp(to, interviewId);

        return ResponseEntity.ok()
                .header("AMP-Email-Allow-Sender", "quick.transfer.gmail@gmail.com")
                .body("{\"message\": \"success!\"}");
    }
}
